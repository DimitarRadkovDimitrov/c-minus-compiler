import absyn.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class CodeGenerator implements AbsynVisitor
{
    public static final int PC = 7;
    public static final int GP = 6;
    public static final int FP = 5;
    public static final int AC = 0;
    public static final int AC1 = 1;   
    public static final int oldFramePtrOffset = 0;
    public static final int returnFramePtrOffset = -1;
    public static final int initFramePtrOffset = -2;

    private List<String> globalInstructions = new ArrayList<>();
    private final String OUTPUT_FILENAME;
    private PrintWriter printWriter;
    private Absyn program;
    private SymbolTable symbolTable;
    private TypeChecker typeChecker;
    private boolean inFunctionSignature = false;
    private int emitLoc = 0;
    private int highEmitLoc = 0;
    private int globalOffset = 0;
    private int entry = 0;

    public CodeGenerator(Absyn program, String outputFilename)
    {
        SymbolTable.Declaration inputFunctionDec = new SymbolTable.Declaration(
            "input",
            new FunctionDec(0, 0, new NameTy(0, 0, NameTy.INT), "input", null, null) 
        );
        SymbolTable.Declaration outputFunctionDec = new SymbolTable.Declaration(
            "output",
            new FunctionDec(
                0,
                0, 
                new NameTy(0, 0, NameTy.VOID),
                "output",
                new VarDecList(new SimpleDec(0, 0, new NameTy(0, 0, NameTy.INT), "x"), null),
                null
            ) 
        );

        this.symbolTable = new SymbolTable();
        this.symbolTable.pushDecToBlock(inputFunctionDec);
        this.symbolTable.pushDecToBlock(outputFunctionDec);
        this.typeChecker = new TypeChecker(symbolTable);
        this.program = program;
        this.OUTPUT_FILENAME = outputFilename;
    }

    public void codeGen() throws IOException
    {
        this.printWriter = new PrintWriter(OUTPUT_FILENAME, "UTF-8");
        emitComment("PRELUDE");
        emitRM("LD", GP, 0, AC, "load gp with maxaddr");
        emitRM("LDA", FP, 0, GP, "copy gp to fp");
        emitRM("ST", AC, 0, AC, "clear content at loc 0");

        int savedLocation = emitSkip(1);
        ((FunctionDec) this.symbolTable.getSymbol("input").dec).funaddr = emitLoc;
        emitComment("INPUT SUBROUTINE");
        emitRM("ST", AC, returnFramePtrOffset, FP, "store return");
        emitRO("IN", 0, 0, 0, "input");
        emitRM("LD", PC, returnFramePtrOffset, FP, "return to caller");

        ((FunctionDec) this.symbolTable.getSymbol("output").dec).funaddr = emitLoc;
        emitComment("OUTPUT SUBROUTINE");
        emitRM("ST", AC, returnFramePtrOffset, FP, "store return");
        emitRM("LD", AC, initFramePtrOffset, FP, "load output value");
        emitRO("OUT", 0, 0, 0, "output");
        emitRM("LD", PC, returnFramePtrOffset, FP, "return to caller");

        this.program.accept(this, 0, false);

        int savedLocation2 = emitSkip(0);
        emitBackup(savedLocation);
        emitRMAbs("LDA", PC, savedLocation2, "");
        emitRestore();

        emitComment("FINALE");
        emitRM("ST", FP, globalOffset + oldFramePtrOffset, FP, "push old frame pointer");
        emitRM("LDA", FP, globalOffset, FP, "push frame");
        emitRM("LDA", AC, 1, PC, "load ac with return pointer");
        emitRMAbs("LDA", PC, entry, "jump to main loc");
        emitRM("LD", FP, oldFramePtrOffset, FP, "pop frame");
        emitRO("HALT", 0, 0, 0, "");
        this.printWriter.close();
    }
    
    public int visit(ArrayDec arrayDec, int offset, boolean isAddress) 
    {
        SymbolTable.Declaration dec;
        int currentBlockLevel = this.symbolTable.getCurrentBlockLevel();
        int framePointer = GP;

        if (arrayDec.size != null)
        {
            arrayDec.offset = offset - arrayDec.size.value + 1;
            if (currentBlockLevel > 1)
            {
                arrayDec.nestLevel = 1;
                framePointer = FP;
                offset -= arrayDec.size.value;
                emitRM("LDC", AC, arrayDec.size.value, AC, "load array size into register");
                emitRM("ST", AC, offset, framePointer, "store array size in memory");
                offset--;
            }
            else
            {
                arrayDec.nestLevel = 0;
                offset -= arrayDec.size.value;
                globalInstructions.add(String.format(
                    "%5s %d, %d(%d)\t%s",
                    "LDC",
                    AC1,
                    arrayDec.size.value,
                    AC,
                    "load array size into ac1"
                ));
                globalInstructions.add(String.format(
                    "%5s %d, %d(%d)\t%s",
                    "ST",
                    AC1,
                    offset,
                    framePointer,
                    "store array size in memory"
                ));
                offset--;
            }

            dec = new SymbolTable.Declaration(arrayDec.name, arrayDec);
            if (!symbolTable.pushDecToBlock(dec))
            {
                SemanticError.redeclarationError(dec);
            }
        }
        else
        {
            arrayDec.isAddress = true;
            arrayDec.offset = offset;
            arrayDec.nestLevel = 1;
            dec = new SymbolTable.Declaration(arrayDec.name, arrayDec);
            
            if (!symbolTable.pushDecToBlock(dec))
            {
                SemanticError.redeclarationError(dec);
            }
            offset--;
        }
        return offset;
    }
    
    public int visit(AssignExp assignExp, int offset, boolean isAddress) 
    {
        assignExp.lhs.accept(this, offset - 1, true);
        emitRM("ST", AC, offset - 1, FP, "store LHS in memory");
        assignExp.rhs.accept(this, offset - 2, false);
        emitRM("ST", AC, offset - 2, FP, "store RHS in memory");
        
        emitRM("LD", AC, offset - 1, FP, "load result of LHS to ac");
        emitRM("LD", AC1, offset - 2, FP, "load result of RHS to ac1");
        emitRM("ST", AC1, 0, AC, "store result of RHS to LHS address");
        emitRM("ST", AC1, offset, FP, "store result of assignment");

        if (assignExp.lhs instanceof SimpleVar)
        {
            SimpleVar simpleVar = (SimpleVar) assignExp.lhs;  
            if ((simpleVar.dec instanceof SimpleDec && ((SimpleDec) simpleVar.dec).typ.typ == NameTy.VOID) ||
                (simpleVar.dec instanceof ArrayDec && ((ArrayDec) simpleVar.dec).typ.typ == NameTy.VOID))
            {
                SemanticError.voidTypeAssignmentError(simpleVar.row + 1, simpleVar.col + 1);
                return offset;
            }
        }
        else if (assignExp.lhs instanceof IndexVar)
        {
            IndexVar indexVar = (IndexVar) assignExp.lhs;            
            if (indexVar.dec.typ.typ == NameTy.VOID)
            {
                SemanticError.voidTypeAssignmentError(indexVar.row + 1, indexVar.col + 1);
                return offset;
            }
        }

        if (!typeChecker.typeCheck(assignExp.rhs, true))
        {
            SemanticError.voidExpressionResultError(assignExp);
        }
        return offset;
    }
    
    public int visit(CallExp callExp, int offset, boolean isAddress) 
    {
        FunctionDec functionDec; 
        VarDecList params;
        ExpList args;

        if (!symbolTable.isDefined(callExp.func))
        {
            SemanticError.undeclaredError(callExp.func, callExp.row + 1, callExp.col + 1);
        }

        functionDec = (FunctionDec) symbolTable.getSymbol(callExp.func).dec;
        params = functionDec.params;
        args = callExp.args;
        
        if (callExp.args != null)
        {
            int i = offset + initFramePtrOffset;
            ExpList expList = callExp.args;
            while (expList != null && expList.head != null)
            {
                expList.head.accept(this, i, false);
                emitRM("ST", AC, i, FP, "save function argument");
                expList = expList.tail;  
                i--;
            }
        }

        emitRM("ST", FP, offset + oldFramePtrOffset, FP, "store old frame ptr");
        emitRM("LDA", FP, offset, FP, "push new frame");
        emitRM("LDA", AC, 1, PC, "save return in ac");
        emitRMAbs("LDA", PC, functionDec.funaddr, "jump to function entry");
        emitRM("LD", FP, oldFramePtrOffset, FP, "pop frame");

        if (!typeChecker.typeCheck(params, args))
        {
            SemanticError.invalidFunctionCallError(callExp);;
        }
        return offset;
    }
    
    public int visit(CompoundExp compoundExp, int offset, boolean isAddress) 
    {
        if (inFunctionSignature)
        {
            inFunctionSignature = false;
            if (compoundExp.decs != null)
            {
                offset = compoundExp.decs.accept(this, offset, false);
            }
            if (compoundExp.exps != null)
            {
                offset = compoundExp.exps.accept(this, offset, false);
            }
        }
        else
        {
            symbolTable.pushBlock();
            if (compoundExp.decs != null)
            {
                offset = compoundExp.decs.accept(this, offset, false);
            }
            if (compoundExp.exps != null)
            {
                offset = compoundExp.exps.accept(this, offset, false);
            }
            symbolTable.popBlock();
        }
        return offset;
    }

    public int visit(DecList decList, int offset, boolean isAddress) 
    {
        // Entering global scope
        while (decList != null)
        {
            if ((decList.head instanceof SimpleDec) || (decList.head instanceof ArrayDec))
            {
                globalOffset = decList.head.accept(this, globalOffset, isAddress);
            }
            else
            {
                decList.head.accept(this, offset, isAddress);
            }
            decList = decList.tail;
        }
        return offset;
        // Leaving global scope
    }
    
    public int visit(ExpList expList, int offset, boolean isAddress) 
    {
        while (expList != null && expList.head != null)
        {
            offset = expList.head.accept(this, offset, isAddress);
            expList = expList.tail;  
        }
        return offset;
    }
    
    public int visit(FunctionDec functionDec, int offset, boolean isAddress) 
    {
        offset = initFramePtrOffset;
        inFunctionSignature = true;
        functionDec.funaddr = emitLoc;
        SymbolTable.Declaration dec = new SymbolTable.Declaration(functionDec.func, functionDec);
        
        // Entering function scope
        if (!symbolTable.pushDecToBlock(dec))
        {
            SemanticError.redeclarationError(dec);
        }

        symbolTable.pushBlock();

        emitComment(functionDec.func + " SUBROUTINE");
        emitRM("ST", AC, returnFramePtrOffset, FP, "store return");

        if (functionDec.func.equals("main"))
        {
            entry = functionDec.funaddr;
            emitGlobalInstructions(globalInstructions);
        }

        if (functionDec.params != null)
        {
            offset = functionDec.params.accept(this, offset, false);
        }
        offset = functionDec.body.accept(this, offset, false);
        
        emitRM("LD", PC, returnFramePtrOffset, FP, "return to caller");

        if (!typeChecker.typeCheck(functionDec))
        {
            SemanticError.invalidFunctionReturnError(functionDec);
        }

        symbolTable.popBlock();
        return offset;
        // Exiting function scope
    }

    public int visit(IfExp ifExp, int offset, boolean isAddress) 
    {
        int afterTestCond;
        int endOfControlStatement;

        ifExp.test.accept(this, offset, false);
        afterTestCond = emitSkip(1);

        ifExp.then.accept(this, offset, false);

        if (ifExp.els != null)
        {
            int beforeElse = emitSkip(1);
            endOfControlStatement = emitSkip(0);

            ifExp.els.accept(this, offset, false);
            int afterElse = emitSkip(0);

            emitBackup(beforeElse);
            emitRMAbs("LDA", PC, afterElse, "jump past false case");
            emitRestore();
        }
        else
        {
            endOfControlStatement = emitSkip(0);
        }

        emitBackup(afterTestCond);
        emitRMAbs("JEQ", AC, endOfControlStatement, "jump forward if loop condition false");
        emitRestore();

        if (!typeChecker.typeCheck(ifExp.test, true))
        {
            SemanticError.voidTestConditionError(ifExp);
        }
        return offset;
    }

    public int visit(IndexVar indexVar, int offset, boolean isAddress)
    {
        int framePointer = GP;
        if (!symbolTable.isDefined(indexVar.name))
        {
            SemanticError.undeclaredError(indexVar.name, indexVar.row + 1, indexVar.col + 1);
            SymbolTable.Declaration dec = new SymbolTable.Declaration(
                indexVar.name, 
                new ArrayDec(
                    indexVar.row,
                    indexVar.col,
                    new NameTy(indexVar.row, indexVar.col, NameTy.ERROR),
                    indexVar.name,
                    new IntExp(indexVar.row, indexVar.col, 0)
                ));
            symbolTable.pushDecToBlock(dec);
        }

        indexVar.dec = (ArrayDec) this.symbolTable.getSymbol(indexVar.name).dec;

        if (indexVar.dec.nestLevel == 1)
        {
            framePointer = FP;
        }

        indexVar.index.accept(this, offset, false);
        if (indexVar.dec.isAddress)
        {
            emitRM("LD", AC1, indexVar.dec.offset, framePointer, String.format("load address pointed to by %s into ac", indexVar.name));
            emitRM("LD", 3, indexVar.dec.offset, framePointer, String.format("load address pointed to by %s into R3", indexVar.name));
            emitRM("LDC", 2, 1, 0, "load 1 into R2");
            emitRM("LD", 4, indexVar.dec.offset, framePointer, "load address of array[0] into R4");
            emitRO("SUB", 4, 4, 2, "decrement address inside R4 by 1 to point to array size");
            emitRM("LD", 4, 0, 4, "load array size into R4");
        }
        else
        {
            emitRM("LDA", AC1, indexVar.dec.offset, framePointer, String.format("load base address of array %s into ac", indexVar.name));
            emitRM("LDA", 3, indexVar.dec.offset, framePointer, String.format("load base address of array %s into R4", indexVar.name));
            emitRM("LD", 4, indexVar.dec.offset - 1, framePointer, "store array size in R5");
        }

        emitRO("ADD", 2, AC, AC1, "get final address of array index and store into R3");
        emitRO("ADD", AC, AC, AC1, "add index offset to base address of array");
        arrayIndexCheck(3, 2, 4);

        if (!isAddress)
        {
            emitRM("LD", AC, 0, AC, String.format("store value of array %s at index into ac", indexVar.name));
        }
        
        if (!typeChecker.typeCheck(indexVar.index, true))
        {
            SemanticError.invalidArrayIndexError(indexVar);
        }
        return offset;
    }

    public int visit(IntExp intExp, int offset, boolean isAddress)
    {
        emitRM("LDC", AC, intExp.value, AC, "load constant into ac");
        return offset;
    }

    public int visit(NameTy nameTy, int offset, boolean isAddress)
    {
        return offset;
    }

    public int visit(NilDec exp, int offset, boolean isAddress)
    {
        return offset;
    }

    public int visit(NilExp exp, int offset, boolean isAddress)
    {
        return offset;
    }

    public int visit(OpExp opExp, int offset, boolean isAddress)
    {
        opExp.left.accept(this, offset - 1, false);
        emitRM("ST", AC, offset - 1, FP, "store LHS in memory");

        opExp.right.accept(this, offset - 2, false);
        emitRM("ST", AC, offset - 2, FP, "store RHS in memory");

        emitRM("LD", AC, offset - 1, FP, "load op1 into ac");
        emitRM("LD", AC1, offset - 2, FP, "load op2 into ac1");

        if (opExp.op == OpExp.PLUS)
        {
            emitRO("ADD", AC, AC, AC1, "add ac and ac1, store into ac");
        }
        else if (opExp.op == OpExp.MINUS)
        {
            emitRO("SUB", AC, AC, AC1, "subtract ac1 from ac, store into ac");
        }
        else if (opExp.op == OpExp.MUL)
        {
            emitRO("MUL", AC, AC, AC1, "multiply ac by ac1, store into ac");
        }
        else if (opExp.op == OpExp.DIV)
        {
            emitRO("DIV", AC, AC, AC1, "divide ac by ac1, store into ac");
        }
        else if (opExp.op == OpExp.EQ)
        {
            emitRO("SUB", AC, AC, AC1, "setup comparison operator");
            emitRM("JEQ", AC, 2, PC, "true if ac is eq to ac1");
            emitRM("LDC", AC, 0, AC, "false case");
            emitRM("LDA", PC, 1, PC, "jump around true case");
            emitRM("LDC", AC, 1, AC, "true case");
        }
        else if (opExp.op == OpExp.NE)
        {
            emitRO("SUB", AC, AC, AC1, "setup comparison operator");
            emitRM("JNE", AC, 2, PC, "true if ac is NOT eq to ac1");
            emitRM("LDC", AC, 0, AC, "false case");
            emitRM("LDA", PC, 1, PC, "jump around true case");
            emitRM("LDC", AC, 1, AC, "true case");
        }
        else if (opExp.op == OpExp.LT)
        {
            emitRO("SUB", AC, AC, AC1, "setup comparison operator");
            emitRM("JLT", AC, 2, PC, "true if ac < ac1");
            emitRM("LDC", AC, 0, AC, "false case");
            emitRM("LDA", PC, 1, PC, "jump around true case");
            emitRM("LDC", AC, 1, AC, "true case");
        }
        else if (opExp.op == OpExp.LE)
        {
            emitRO("SUB", AC, AC, AC1, "setup comparison operator");
            emitRM("JLE", AC, 2, PC, "true if ac <= ac1");
            emitRM("LDC", AC, 0, AC, "false case");
            emitRM("LDA", PC, 1, PC, "jump around true case");
            emitRM("LDC", AC, 1, AC, "true case");
        }
        else if (opExp.op == OpExp.GT)
        {
            emitRO("SUB", AC, AC, AC1, "setup comparison operator");
            emitRM("JGT", AC, 2, PC, "true if ac > ac1");
            emitRM("LDC", AC, 0, AC, "false case");
            emitRM("LDA", PC, 1, PC, "jump around true case");
            emitRM("LDC", AC, 1, AC, "true case");
        }
        else if (opExp.op == OpExp.GE)
        {
            emitRO("SUB", AC, AC, AC1, "setup comparison operator");
            emitRM("JGE", AC, 2, PC, "true if ac >= ac1");
            emitRM("LDC", AC, 0, AC, "false case");
            emitRM("LDA", PC, 1, PC, "jump around true case");
            emitRM("LDC", AC, 1, AC, "true case");
        }
        emitRM("ST", AC, offset, FP, "store opExp result");

        if (!typeChecker.typeCheck(opExp, true))
        {
            SemanticError.voidExpressionResultError(opExp);
        } 
        return offset;
    }

    public int visit(ReturnExp returnExp, int offset, boolean isAddress)
    {
        returnExp.exp.accept(this, offset, false);
        emitRM("LD", PC, returnFramePtrOffset, FP, "return to caller");
        return offset;
    }
    
    public int visit(SimpleDec simpleDec, int offset, boolean isAddress)
    {
        SymbolTable.Declaration dec;
        int currentBlockLevel = symbolTable.getCurrentBlockLevel();

        simpleDec.offset = offset;
        simpleDec.nestLevel = currentBlockLevel > 1 ? 1 : 0;
        dec = new SymbolTable.Declaration(simpleDec.name, simpleDec);

        if (!symbolTable.pushDecToBlock(dec))
        {
            SemanticError.redeclarationError(dec);
        }
        return offset - 1;
    }

    public int visit(SimpleVar simpleVar, int offset, boolean isAddress)
    {
        int framePointer = GP;
        if (!symbolTable.isDefined(simpleVar.name))
        {
            SemanticError.undeclaredError(simpleVar.name, simpleVar.row + 1, simpleVar.col + 1);
            SymbolTable.Declaration dec = new SymbolTable.Declaration(
                simpleVar.name, 
                new SimpleDec(
                    simpleVar.row,
                    simpleVar.col,
                    new NameTy(simpleVar.row, simpleVar.col, NameTy.ERROR),
                    simpleVar.name
                ));
            symbolTable.pushDecToBlock(dec);
        }

        simpleVar.dec = this.symbolTable.getSymbol(simpleVar.name).dec;
        if (simpleVar.dec instanceof SimpleDec)
        {
            SimpleDec simpleDec = (SimpleDec) simpleVar.dec;
            if (simpleDec.nestLevel == 1)
            {
                framePointer = FP;
            }
            if (isAddress)
            {
                emitRM("LDA", AC, simpleDec.offset, framePointer, String.format("load address of variable %s into ac", simpleVar.name));
            }
            else
            {
                emitRM("LD", AC, simpleDec.offset, framePointer, String.format("store value of variable %s into ac", simpleVar.name));
            }
        }
        else if (simpleVar.dec instanceof ArrayDec)
        {
            ArrayDec arrayDec = (ArrayDec) simpleVar.dec;
            if (arrayDec.nestLevel == 1)
            {
                framePointer = FP;
            }
            if (arrayDec.isAddress)
            {
                emitRM("LD", AC, arrayDec.offset, framePointer, String.format("store address pointed to by %s into ac", simpleVar.name));
            }
            else
            {
                emitRM("LDA", AC, arrayDec.offset, framePointer, String.format("store address of %s into ac", simpleVar.name));
            }
        }
        return offset;
    }

    public int visit(VarDecList varDecList, int offset, boolean isAddress)
    {
        while (varDecList != null && varDecList.head != null)
        {
            offset = varDecList.head.accept(this, offset, isAddress);
            varDecList = varDecList.tail;
        }
        return offset;
    }

    public int visit(VarExp varExp, int offset, boolean isAddress)
    {
        varExp.variable.accept(this, offset, isAddress);
        return offset;
    }

    public int visit(WhileExp whileExp, int offset, boolean isAddress)
    {
        int savedLocation1 = emitSkip(0);
        whileExp.test.accept(this, offset, false);

        int savedLocation2 = emitSkip(1);
        whileExp.body.accept(this, offset, false);
        emitRMAbs("LDA", PC, savedLocation1, "jump back to test condition");

        int savedLocation3 = emitSkip(0);
        emitBackup(savedLocation2);
        emitRMAbs("JEQ", AC, savedLocation3, "jump forward if loop condition false");
        emitRestore();

        if (!typeChecker.typeCheck(whileExp.test, true))
        {
            SemanticError.voidTestConditionError(whileExp);
        }
        return offset;
    }

    private void emitBackup(int loc)
    {
        if (loc > highEmitLoc)
        {
            emitComment("BUG in emitBackup");
        }
        emitLoc = loc;
    } 

    private void emitComment(String comment)
    {
        comment = String.format("* %s", comment);
        this.printWriter.println(comment);
    }

    private void emitGlobalInstructions(List<String> instructions)
    {
        for (String instruction: instructions)
        {
            instruction = String.format("%3d: %s", emitLoc, instruction);
            this.printWriter.println(instruction);
            
            emitLoc += 1;
            if (highEmitLoc < emitLoc)
            {
                highEmitLoc = emitLoc;
            }
        }
    }

    private void emitRestore()
    {
        emitLoc = highEmitLoc;
    }

    private void emitRO(String op, int reg, int operand1, int operand2, String comment)
    {
        String instruction = String.format(
            "%3d: %5s %d, %d, %d\t%s",
            emitLoc,
            op,
            reg,
            operand1,
            operand2,
            comment
        );
        this.printWriter.println(instruction);

        emitLoc += 1;
        if (highEmitLoc < emitLoc)
        {
            highEmitLoc = emitLoc;
        }
    }

    private void emitRM(String op, int reg, int offset, int reg2, String comment)
    {
        String instruction = String.format(
            "%3d: %5s %d, %d(%d)\t%s",
            emitLoc,
            op,
            reg,
            offset,
            reg2,
            comment
        );
        this.printWriter.println(instruction);

        emitLoc += 1;
        if (highEmitLoc < emitLoc)
        {
            highEmitLoc = emitLoc;
        }
    }

    private void emitRMAbs(String op, int reg, int addr, String comment)
    {
        String instruction = String.format(
            "%3d: %5s %d, %d(%d)\t%s", 
            emitLoc, 
            op, 
            reg, 
            addr - (emitLoc + 1), 
            PC,
            comment
        );
        this.printWriter.println(instruction);

        emitLoc += 1;
        if (highEmitLoc < emitLoc)
        {
            highEmitLoc = emitLoc;
        }
    }

    private int emitSkip(int distance)
    {
        int i = emitLoc;
        emitLoc += distance;

        if (highEmitLoc < emitLoc)
        {
            highEmitLoc = emitLoc;
        }
        return i;
    }

    private void arrayIndexCheck(int regOriginalAddr, int regNewAddr, int regArraySize)
    {
        emitRO("SUB", regOriginalAddr, regNewAddr, regOriginalAddr, "subtract new address from original");
        emitRM("JLT", regOriginalAddr, 3, PC, "jump to out of range below exception");
        emitRO("SUB", regOriginalAddr, regArraySize, regOriginalAddr, "subtract array size from element index");
        emitRM("JLE", regOriginalAddr, 3, PC, "jump to out of range above exception");
        emitRM("LDA", PC, 5, PC, "jump past error state");
        emitRM("LDC", regOriginalAddr, -1000000, AC, "out of range below");
        emitRM("LDA", PC, 1, PC, "jump over other exception code");
        emitRM("LDC", regOriginalAddr, -2000000, AC, "out of range above");
        emitRO("OUT", regOriginalAddr, AC, AC, "print error code");
        emitRO("HALT", AC, AC, AC, "terminate due to exception");
    }

}