/*
* A complete 6502 instruction set implementation
* Written by Liam Skirrow, 2018
*/

package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

import java.util.List;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("gui.fxml"));
        primaryStage.setTitle("6502 Simulator");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

//responsible for assembling the user input (assembly code)
class Assemble{
    static void assemble(byte[] mem, List<Label> labelList, String inputBuf, Register A, Register X, Register Y,
                         bigRegister programCounter, FLAGRegister registerFlag, bigRegister stackPointer, javafx.scene.control.TextArea hexdumpDisplay,
                         javafx.scene.control.TextArea registerDisplay, javafx.scene.control.TextArea terminal) {

        //read the first line and initialise program counter...
        int i, j=0, k=0;
        for (i = 0; i < inputBuf.length(); i++) {

            //detect the end-of-assembly flag  BRK_ <-- don't forget the underscore to avoid an array out of bounds exception!
            if(inputBuf.substring(i, i+4).equals("BRK_")){
                System.out.println("found BRK__ command");
                //terminal.setText("Program Finished");
                break; //we've found the end of the input -> exit the assembler method
            }

            //***************** Found a comment, increment counter to next new line
            while(inputBuf.charAt(i) == ';'){
                System.out.println("Found a comment");
                while(inputBuf.charAt(i++) != '\n'){
                    //do nothing, simply increment the counter to the next newline
                }
            }
            //*****************


            //stray spacebar or new line char, ignore and carry on...
            if(inputBuf.charAt(i) == ' ' || inputBuf.charAt(i) == '\n') {
                j=0; //do nothing
            }

            //****************************************************
            //Load into a register

            //'LDA' instruction
            else if(inputBuf.substring(i, i+4).equals("LDA ")) {
                i = instructionLDA(mem, inputBuf, i, X, Y);
            }
            //'LDX' instruction
            else if(inputBuf.substring(i, i+4).equals("LDX ")) {
                i = instructionLDX(mem, inputBuf, i, Y);
            }
            //'LDY' instruction
            else if(inputBuf.substring(i, i+4).equals("LDY ")) {
                i = instructionLDY(mem, inputBuf, i, X);
            }
            //****************************************************
            //Transfer between registers

            //'TAX' instruction
            else if(inputBuf.substring(i, i+3).equals("TAX")){
                //Register.transferRegisterValue(A, X);
                i = instructionTAX(mem,inputBuf,i,A,X);
            }
            //'TAY' instruction
            else if(inputBuf.substring(i, i+3).equals("TAY")){
                //Register.transferRegisterValue(A, Y);
                i = instructionTAY(mem,inputBuf,i,A,Y);
            }
            //'TSX' instruction
            else if(inputBuf.substring(i, i+3).equals("TSX")){
                //Register.transferRegisterValue(stackPointer, X);
                i = instructionTSX(mem,inputBuf,i,stackPointer,X);
            }
            //'TXA' instruction
            else if(inputBuf.substring(i, i+3).equals("TXA")){
                //Register.transferRegisterValue(X, A);
                i = instructionTXA(mem,inputBuf,i,X,A);
            }
            //'TXS' instruction
            else if(inputBuf.substring(i, i+3).equals("TXS")){
                //Register.transferRegisterValue(X, stackPointer);
                i = instructionTXS(mem,inputBuf,i,X,stackPointer);
            }
            //'TYA' instruction
            else if(inputBuf.substring(i, i+3).equals("TYA")){
                //Register.transferRegisterValue(Y, A);
                i = instructionTYA(mem,inputBuf,i,Y,A);
            }
            //****************************************************
            //Store into a memory location

            //'STA' instruction
            else if(inputBuf.substring(i, i+4).equals("STA ")){
                i = instructionSTA(mem, inputBuf, i, X, Y);
            }
            //'STX' instruction
            else if(inputBuf.substring(i, i+4).equals("STX ")){
                i = instructionSTX(mem, inputBuf, i, Y);
            }
            //'STY' instruction
            else if(inputBuf.substring(i, i+4).equals("STY ")){
                i = instructionSTY(mem, inputBuf, i, X);
            }
            //****************************************************
            //Arithmetic and Logic

            //'ADC' instruction
            else if(inputBuf.substring(i, i+4).equals("ADC ")){
                i = instructionADC(mem, inputBuf, i, X, Y);
            }
            //'AND' instruction
            else if(inputBuf.substring(i, i+4).equals("AND ")){
                i = instructionAND(mem, inputBuf, i, X, Y);
            }
            //'ASL' instruction
            else if(inputBuf.substring(i, i+4).equals("ASL ")){
                i = instructionASL(mem, inputBuf, i, X, Y);
            }
            //'EOR' instruction
            else if(inputBuf.substring(i, i+4).equals("EOR ")){
                i = instructionEOR(mem, inputBuf, i, X, Y);
            }
            //'ORA' instruction
            else if(inputBuf.substring(i, i+4).equals("ORA ")){
                i = instructionEOR(mem, inputBuf, i, X, Y);
            }
            //****************************************************
            //Branch instructions

            //'BCC' instruction
            else if(inputBuf.substring(i, i+4).equals("BCC ")){
                i = instructionBCC(mem, inputBuf, labelList, i, X, Y);
            }
            //'BCS' instruction
            else if(inputBuf.substring(i, i+4).equals("BCS ")){
                i = instructionBCS(mem, inputBuf, labelList, i, X, Y);
            }
            //'BEQ' instruction
            else if(inputBuf.substring(i, i+4).equals("BEQ ")){
                i = instructionBEQ(mem, inputBuf, labelList, i, X, Y);
            }
            //'BIT' instruction
            else if(inputBuf.substring(i, i+4).equals("BIT ")){
                i = instructionBIT(mem, inputBuf, i, X, Y);
            }
            //'BMI' instruction
            else if(inputBuf.substring(i, i+4).equals("BMI ")){
                i = instructionBMI(mem, inputBuf, labelList, i, X, Y);
            }
            //'BNE' instruction
            else if(inputBuf.substring(i, i+4).equals("BNE ")){
                i = instructionBNE(mem, inputBuf, labelList, i, X, Y);
            }
            //'BPL' instruction
            else if(inputBuf.substring(i, i+4).equals("BPL ")){
                i = instructionBPL(mem, inputBuf, labelList, i, X, Y);
            }
            //'BRK' instruction
            else if(inputBuf.substring(i, i+3).equals("BRK")){
                i = instructionBRK(mem, inputBuf, labelList, i, X, Y);
            }
            //'BVC' instruction
            else if(inputBuf.substring(i, i+4).equals("BVC ")){
                i = instructionBVC(mem, inputBuf, labelList, i, X, Y);
            }
            //'BVS' instruction
            else if(inputBuf.substring(i, i+4).equals("BVS ")){
                i = instructionBVS(mem, inputBuf, labelList, i, X, Y);
            }
            //****************************************************
            //Clear flag bits
            //'CLC' instruction
            else if(inputBuf.substring(i, i+3).equals("CLC")){
                i = instructionCLC(mem, inputBuf, labelList, i, X, Y);
            }
            //'CLD' instruction
            else if(inputBuf.substring(i, i+3).equals("CLD")){
                i = instructionCLD(mem, inputBuf, labelList, i, X, Y);
            }
            //'CLI' instruction
            else if(inputBuf.substring(i, i+3).equals("CLI")){
                i = instructionCLI(mem, inputBuf, labelList, i, X, Y);
            }
            //'CLV' instruction
            else if(inputBuf.substring(i, i+3).equals("CLV")){
                i = instructionCLV(mem, inputBuf, labelList, i, X, Y);
            }
            //****************************************************
            //Set flag bits
            //'SEC' instruction
            else if(inputBuf.substring(i, i+3).equals("SEC")){
                i = instructionSEC(mem, inputBuf, labelList, i, X, Y);
            }
            //'SED' instruction
            else if(inputBuf.substring(i, i+3).equals("SED")){
                i = instructionSED(mem, inputBuf, labelList, i, X, Y);
            }
            //'SEI' instruction
            else if(inputBuf.substring(i, i+3).equals("SEI")){
                i = instructionSEI(mem, inputBuf, labelList, i, X, Y);
            }
            //****************************************************
            //Compare register values with memory/immediates
            //'CMP' instruction
            else if(inputBuf.substring(i, i+4).equals("CMP ")){
                i = instructionCMP(mem, inputBuf, i, X, Y);
            }
            //'CPX' instruction
            else if(inputBuf.substring(i, i+4).equals("CPX ")){
                i = instructionCPX(mem, inputBuf, i, X, Y);
            }
            //'CPY' instruction
            else if(inputBuf.substring(i, i+4).equals("CPY ")){
                i = instructionCPY(mem, inputBuf, i, X, Y);
            }
            //****************************************************
            //Decrement memory/registers by one
            //'DEC' instruction
            else if(inputBuf.substring(i, i+4).equals("DEC ")){
                i = instructionDEC(mem, inputBuf, i, X, Y);
            }
            //'DEX' instruction
            else if(inputBuf.substring(i, i+3).equals("DEX")){
                i = instructionDEX(mem, inputBuf, i, X, Y);
            }
            //'DEY' instruction
            else if(inputBuf.substring(i, i+3).equals("DEY")){
                i = instructionDEY(mem, inputBuf, i, X, Y);
            }
            //****************************************************
            //Increment memory/registers by one
            //'INC' instruction
            else if(inputBuf.substring(i, i+4).equals("INC ")){
                i = instructionINC(mem, inputBuf, i, X, Y);
            }
            //'INX' instruction
            else if(inputBuf.substring(i, i+3).equals("INX")){
                i = instructionINX(mem, inputBuf, i, X, Y);
            }
            //'INY' instruction
            else if(inputBuf.substring(i, i+3).equals("INY")){
                i = instructionINY(mem, inputBuf, i, X, Y);
            }
            //****************************************************
            //'JMP' instruction
            else if(inputBuf.substring(i, i+4).equals("JMP ")){
                i = instructionJMP(mem, inputBuf, labelList, i, X, Y);
            }
            //****************************************************
            //'JSR' instruction
            else if(inputBuf.substring(i, i+4).equals("JSR ")){
                i = instructionJSR(mem, inputBuf, labelList, i, X, Y);
            }
            //****************************************************
            //'LSR' instruction
            else if(inputBuf.substring(i, i+4).equals("LSR ")){
                i = instructionLSR(mem, inputBuf, i, X, Y);
            }
            //****************************************************
            //'NOP' instruction
            else if(inputBuf.substring(i, i+3).equals("NOP")){
                i = instructionNOP(mem, inputBuf, i, X, Y);
            }
            //****************************************************
            //Push/pop stack
            //'PHA' instruction
            else if(inputBuf.substring(i, i+3).equals("PHA")){
                i = instructionPHA(mem, inputBuf, i, X, Y);
            }
            //'PHP' instruction
            else if(inputBuf.substring(i, i+3).equals("PHP")){
                i = instructionPHP(mem, inputBuf, i, X, Y);
            }
            //'PLA' instruction
            else if(inputBuf.substring(i, i+3).equals("PLA")){
                i = instructionPLA(mem, inputBuf, i, X, Y);
            }
            //'PLP' instruction
            else if(inputBuf.substring(i, i+3).equals("PLP")){
                i = instructionPLP(mem, inputBuf, i, X, Y);
            }
            //****************************************************
            //Rotate right/left
            //'ROL' instruction
            else if(inputBuf.substring(i, i+4).equals("ROL ")){
                i = instructionROL(mem, inputBuf, i, X, Y);
            }
            //'ROR' instruction
            else if(inputBuf.substring(i, i+4).equals("ROR ")){
                i = instructionROR(mem, inputBuf, i, X, Y);
            }
            //****************************************************




            //either a syntax error or a label, determine which
            else{
                //increment through until a ' ' or a '\n' is found (typo/error), or a ':' is found (label)
                for(;;i++){
                    if(inputBuf.charAt(i) == ' ' || inputBuf.charAt(i) == '\n'){ //found a typo
                        System.out.println("syntax error in parent method: " + inputBuf.charAt(i) + ", " + i);

                        //*******************************
                        //TODO update terminal with syntax error and line number?????
                        //*******************************

                        break;
                    }
                    else if(inputBuf.charAt(i) == ':'){ //found a label
                        System.out.println("found a label");
                        Label l = new Label();

                        //find the label name
                        for(k=i; ; k--){
                            //System.out.print(inputBuf.charAt(k));
                            if(k >= 0){
                                if ((inputBuf.charAt(k) == ' ' || inputBuf.charAt(k) == '\n')) {
                                    //do nothing, cycle back to the beginning of the label
                                    break;
                                }
                            }
                            else{
                                break;
                            }
                        }
                        l.name = inputBuf.substring(k+1,i);
                        l.address = Controller.PCIndex;
                        labelList.add(l);
                        break;
                    }
                }
            }
        }
        //***** call the memory/register screen update method here *****
        Controller.updateDisplays(mem, hexdumpDisplay, registerDisplay, A, X, Y, registerFlag, programCounter);
    }

    //interpret the LDA instruction
    private static int instructionLDA(byte[] mem, String inputBuf, int i, Register X, Register Y){
        //LDA #$hexIMM (LDA #$FF) //TODO have a byte of this bug
        if(inputBuf.substring(i+4,i+6).equals("#$")){
            System.out.println("in LDA #$");
            byte hexVal = stringToHex(inputBuf.substring(i+6,i+8), 2);
            mem[Controller.PCIndex++] = (byte) 0xA9;
            mem[Controller.PCIndex++] = hexVal;
            i+=7;
        }
        //LDA ZP (LDA $FF)
        else if(inputBuf.charAt(i+4) == '$' && (inputBuf.charAt(i+7) == '\n' || inputBuf.charAt(i+7) == ' ') || inputBuf.charAt(i+7) == '\0'){
            System.out.println("in LDA $FF");
            byte hexVal = stringToHex(inputBuf.substring(i+5,i+7), 2);
            mem[Controller.PCIndex++] = (byte) 0xA5;
            mem[Controller.PCIndex++] = hexVal;
            i+=6;
        }
        //LDA AbsoluteAddress (LDA $0FFF) the absolute address must take up TWO bytes
        else if(inputBuf.charAt(i+4) == '$' && (inputBuf.charAt(i+9) == '\n' || inputBuf.charAt(i+9) == ' ' || inputBuf.charAt(i+9) == '\0')){
            System.out.println("in LDA $0FFF");
            byte hexVal1 = stringToHex(inputBuf.substring(i+5,i+7), 2);
            byte hexVal2 = stringToHex(inputBuf.substring(i+7,i+9), 2);   //changed from i+7,i+8, ... strLength: 1
            mem[Controller.PCIndex++] = (byte) 0xAD;

            System.out.println("hexVal1: " + hexVal1 + " hexVal2: " + hexVal2);

            mem[Controller.PCIndex++] = hexVal2; //little endian order, least significant byte
            mem[Controller.PCIndex++] = hexVal1; //little endian order, most significant byte
            i+=8;  //changed from 7
        }
        //LDA ZP, X (LDA $FF, X)
        else if(inputBuf.charAt(i+7) == ',' && (inputBuf.charAt(i+10) == '\n' || inputBuf.charAt(i+10) == ' ' || inputBuf.charAt(i+10) == '\0')){
            System.out.println("in LDA ZP, X");
            byte hexVal1 = stringToHex(inputBuf.substring(i+5,i+7), 2);
            mem[Controller.PCIndex++] = (byte) 0xB5;
            mem[Controller.PCIndex++] = hexVal1;
            i+=9;
        }
        //LDA Abs, X (LDA $0FFF, X)
        else if((inputBuf.charAt(i+9) == ',' && inputBuf.charAt(i+11) == 'X') && (inputBuf.charAt(i+12) == '\n' || inputBuf.charAt(i+12) == ' ' || inputBuf.charAt(i+12) == '\0')){
            System.out.println("in LDA Abs, X");
            //int hexVal1 = stringToHex(inputBuf.substring(i+5,i+9), 4);   //previously: i+5,i+8 -> strLength = 3
            //String addVal = Integer.toHexString(hexVal1 + X.val);
            //hexVal1 = stringToHex(addVal.substring(0,2), 2);
            //int hexVal2 = stringToHex(addVal.substring(2,3), 1);

            byte hexVal1 = stringToHex(inputBuf.substring(i+5,i+7), 2);   //previously: i+5,i+8 -> strLength = 3
            byte hexVal2 = stringToHex(inputBuf.substring(i+7,i+9), 2);

            mem[Controller.PCIndex++] = (byte) 0xBD;
            mem[Controller.PCIndex++] = hexVal2;  //little endian order, least significant byte
            mem[Controller.PCIndex++] = hexVal1;  //little endian order, most significant byte
            i+=11;  //changed from 10
        }
        //LDA Abs, Y (LDA $0FFF, Y)
        else if((inputBuf.charAt(i+8) != ')' && inputBuf.charAt(i+9) == ',' && inputBuf.charAt(i+11) == 'Y') && (inputBuf.charAt(i+12) == '\n' || inputBuf.charAt(i+12) == ' ' || inputBuf.charAt(i+12) == '\0')){
            System.out.println("in LDA Abs, Y");
            //int hexVal1 = stringToHex(inputBuf.substring(i+5,i+8), 3);
            //String addVal = Integer.toHexString(hexVal1 + Y.val);
            //hexVal1 = stringToHex(addVal.substring(0,2), 2);
            //int hexVal2 = stringToHex(addVal.substring(2,3), 1);

            byte hexVal1 = stringToHex(inputBuf.substring(i+5,i+7), 2);   //previously: i+5,i+8 -> strLength = 3
            byte hexVal2 = stringToHex(inputBuf.substring(i+7,i+9), 2);

            mem[Controller.PCIndex++] = (byte) 0xB9;
            mem[Controller.PCIndex++] = hexVal2;  //little endian order, least significant byte
            mem[Controller.PCIndex++] = hexVal1;  //little endian order, most significant byte
            i+=11;   //changed from 10
        }
        //LDA (ZP,X) ( LDA ($FF, X) )
        else if((inputBuf.charAt(i+4) == '(' && inputBuf.charAt(i+11) == ')') && (inputBuf.charAt(i+12) == '\n' || inputBuf.charAt(i+12) == ' ' || inputBuf.charAt(i+12) == '\0') ){
            System.out.println("in LDA (ZP, X)");
            byte hexVal1 = stringToHex(inputBuf.substring(i+6,i+8), 2);

            mem[Controller.PCIndex++] = (byte) 0xA1;
            mem[Controller.PCIndex++] = hexVal1;
            i+=11;
        }
        //LDA (ZP), Y   ( LDA ($FF), Y )
        else if((inputBuf.charAt(i+4) == '(' && inputBuf.charAt(i+8) == ')') && (inputBuf.charAt(i+12) == '\n' || inputBuf.charAt(i+12) == ' ' || inputBuf.charAt(i+12) == '\0') ){
            System.out.println("in LDA (ZP), Y");
            byte hexVal1 = stringToHex(inputBuf.substring(i+6,i+8), 2);

            mem[Controller.PCIndex++] = (byte) 0xB1;
            mem[Controller.PCIndex++] = hexVal1;
            i+=11;
        }
        else {
            System.out.println("syntax error in child LDA method: " + inputBuf.charAt(i) + ", " + i);
        }
        return i;
    }

    private static int instructionLDX(byte[] mem, String inputBuf, int i, Register Y){
        //LDX #$hexIMM (LDX #$FF)
        if(inputBuf.substring(i+4,i+6).equals("#$")){
            System.out.println("in LDX #$");
            byte hexVal = stringToHex(inputBuf.substring(i+6,i+8), 2);
            mem[Controller.PCIndex++] = (byte) 0xA2;
            mem[Controller.PCIndex++] = hexVal;
            i+=7;
        }
        //LDX ZP (LDX $FF)
        else if(inputBuf.charAt(i+4) == '$' && (inputBuf.charAt(i+7) == '\n' || inputBuf.charAt(i+7) == ' ') || inputBuf.charAt(i+7) == '\0'){
            System.out.println("in LDX $FF");
            byte hexVal = stringToHex(inputBuf.substring(i+5,i+7), 2);
            mem[Controller.PCIndex++] = (byte) 0xA6;
            mem[Controller.PCIndex++] = hexVal;
            i+=6;
        }
        //LDX AbsoluteAddress (LDX $0FFF) the absolute address must take up TWO bytes
        else if(inputBuf.charAt(i+4) == '$' && (inputBuf.charAt(i+9) == '\n' || inputBuf.charAt(i+9) == ' ' || inputBuf.charAt(i+9) == '\0')){
            System.out.println("in LDX $FFF");
            byte hexVal1 = stringToHex(inputBuf.substring(i+5,i+7), 2);
            byte hexVal2 = stringToHex(inputBuf.substring(i+7,i+9), 2);
            mem[Controller.PCIndex++] = (byte) 0xAE;
            mem[Controller.PCIndex++] = hexVal2;  //little endian order, least significant byte
            mem[Controller.PCIndex++] = hexVal1;  //little endian order, most significant byte
            i+=8;
        }
        //LDX ZP, Y (LDX $FF, Y)
        else if(inputBuf.charAt(i+7) == ',' && (inputBuf.charAt(i+10) == '\n' || inputBuf.charAt(i+10) == ' ' || inputBuf.charAt(i+10) == '\0')){
            System.out.println("in LDX ZP, Y");
            byte hexVal1 = stringToHex(inputBuf.substring(i+5,i+7), 2);
            mem[Controller.PCIndex++] = (byte) 0xB6;
            mem[Controller.PCIndex++] = hexVal1;
            i+=9;
        }
        //LDX Abs, Y (LDX $0FFF, Y)
        else if(inputBuf.charAt(i+9) == ',' && (inputBuf.charAt(i+12) == '\n' || inputBuf.charAt(i+12) == ' ' || inputBuf.charAt(i+12) == '\0')){
            System.out.println("in LDX Abs, Y");
            //int hexVal1 = stringToHex(inputBuf.substring(i+5,i+8), 3);
            //String addVal = Integer.toHexString(hexVal1 + Y.val);
            //hexVal1 = stringToHex(addVal.substring(0,2), 2);
            //int hexVal2 = stringToHex(addVal.substring(2,3), 1);

            byte hexVal1 = stringToHex(inputBuf.substring(i+5,i+7), 2);
            byte hexVal2 = stringToHex(inputBuf.substring(i+7,i+9), 2);


            mem[Controller.PCIndex++] = (byte) 0xBE;
            mem[Controller.PCIndex++] = hexVal2;  //little endian order, least significant byte
            mem[Controller.PCIndex++] = hexVal1;  //little endian order, most significant byte
            i+=11;   //changed from 10
        }
        else {
            System.out.println("syntax error in child LDX method: " + inputBuf.charAt(i) + ", " + i);
        }

        return i;
    }

    private static int instructionLDY(byte[] mem, String inputBuf, int i, Register X) {
        //LDY #$hexIMM (LDY #$FF)
        if (inputBuf.substring(i + 4, i + 6).equals("#$")) {
            System.out.println("in LDY #$");
            byte hexVal = stringToHex(inputBuf.substring(i + 6, i + 8), 2);
            mem[Controller.PCIndex++] = (byte) 0xA0;
            mem[Controller.PCIndex++] = hexVal;
            i += 7;
        }
        //LDY ZP (LDY $FF)
        else if (inputBuf.charAt(i + 4) == '$' && (inputBuf.charAt(i + 7) == '\n' || inputBuf.charAt(i + 7) == ' ') || inputBuf.charAt(i + 7) == '\0') {
            System.out.println("in LDY $FF");
            byte hexVal = stringToHex(inputBuf.substring(i + 5, i + 7), 2);
            mem[Controller.PCIndex++] = (byte) 0xA4;
            mem[Controller.PCIndex++] = hexVal;
            i += 6;
        }
        //LDY AbsoluteAddress (LDY $0FFF) the absolute address must take up TWO bytes
        else if (inputBuf.charAt(i + 4) == '$' && (inputBuf.charAt(i + 9) == '\n' || inputBuf.charAt(i + 9) == ' ' || inputBuf.charAt(i + 9) == '\0')) {
            System.out.println("in LDY $0FFF");
            byte hexVal1 = stringToHex(inputBuf.substring(i + 5, i + 7), 2);
            byte hexVal2 = stringToHex(inputBuf.substring(i + 7, i + 9), 2);
            mem[Controller.PCIndex++] = (byte) 0xAC;
            mem[Controller.PCIndex++] = hexVal2;  //little endian order, least significant byte
            mem[Controller.PCIndex++] = hexVal1;  //little endian order, most significant byte
            i+=8;   //changed from 7
        }
        //LDY ZP, X (LDY $FF, X)
        else if (inputBuf.charAt(i + 7) == ',' && (inputBuf.charAt(i + 10) == '\n' || inputBuf.charAt(i + 10) == ' ' || inputBuf.charAt(i + 10) == '\0')) {
            System.out.println("in LDY ZP, X");
            byte hexVal1 = stringToHex(inputBuf.substring(i + 5, i + 7), 2);
            mem[Controller.PCIndex++] = (byte) 0xB4;
            mem[Controller.PCIndex++] = hexVal1;
            i += 9;
        }
        //LDY Abs, X (LDY $0FFF, X)
        else if (inputBuf.charAt(i + 9) == ',' && (inputBuf.charAt(i + 12) == '\n' || inputBuf.charAt(i + 12) == ' ' || inputBuf.charAt(i + 12) == '\0')) {
            System.out.println("in LDY Abs, X");
            //int hexVal1 = stringToHex(inputBuf.substring(i + 5, i + 8), 3);
            //String addVal = Integer.toHexString(hexVal1 + X.val);
            //hexVal1 = stringToHex(addVal.substring(0, 2), 2);
            //int hexVal2 = stringToHex(addVal.substring(2, 3), 1);

            byte hexVal1 = stringToHex(inputBuf.substring(i + 5, i + 7), 2);
            byte hexVal2 = stringToHex(inputBuf.substring(i + 7, i + 9), 2);

            mem[Controller.PCIndex++] = (byte) 0xBC;
            mem[Controller.PCIndex++] = hexVal2;  //little endian order, least significant byte
            mem[Controller.PCIndex++] = hexVal1;  //little endian order, most significant byte
            i += 11;   //changed from 10
        }
        else {
            System.out.println("syntax error in child LDY method: " + inputBuf.charAt(i) + ", " + i);
        }
        return i;
    }

    //TAX
    private static int instructionTAX(byte[] mem, String inputBuf, int i, Register A, Register X){
        System.out.println("in TAX");
        mem[Controller.PCIndex++] = (byte) 0xAA;
        i+=3;
        return i;
    }
    //TAY
    private static int instructionTAY(byte[] mem, String inputBuf, int i, Register A, Register Y){
        System.out.println("in TAY");
        mem[Controller.PCIndex++] = (byte) 0xA8;
        i+=3;
        return i;
    }
    //TSX
    private static int instructionTSX(byte[] mem, String inputBuf, int i, bigRegister stackPointer, Register X){
        System.out.println("in TSX");
        mem[Controller.PCIndex++] = (byte) 0xBA;
        i+=3;
        return i;
    }
    //TXA
    private static int instructionTXA(byte[] mem, String inputBuf, int i, Register X, Register A){
        System.out.println("in TXA");
        mem[Controller.PCIndex++] = (byte) 0x8A;
        i+=3;
        return i;
    }
    //TXS
    private static int instructionTXS(byte[] mem, String inputBuf, int i, Register X, bigRegister stackPointer){
        System.out.println("in TXS");
        mem[Controller.PCIndex++] = (byte) 0x9A;
        i+=3;
        return i;
    }
    //TYA
    private static int instructionTYA(byte[] mem, String inputBuf, int i, Register Y, Register A){
        System.out.println("in TYA");
        mem[Controller.PCIndex++] = (byte) 0x98;
        i+=3;
        return i;
    }


    private static int instructionSTA(byte[] mem, String inputBuf, int i, Register X, Register Y){
        //STA ZP (STA $FF)
        if(inputBuf.charAt(i + 4) == '$' && (inputBuf.charAt(i + 7) == '\n' || inputBuf.charAt(i + 7) == ' ') || inputBuf.charAt(i + 7) == '\0') {
            System.out.println("in STA $FF");
            byte hexVal = stringToHex(inputBuf.substring(i + 5, i + 7), 2);
            mem[Controller.PCIndex++] = (byte) 0x85;
            mem[Controller.PCIndex++] = hexVal;
            i += 6;
        }
        //STA ZP, X (STA $FF, X)
        else if (inputBuf.charAt(i + 7) == ',' && (inputBuf.charAt(i + 10) == '\n' || inputBuf.charAt(i + 10) == ' ' || inputBuf.charAt(i + 10) == '\0')) {
            System.out.println("in STA ZP, X");
            byte hexVal1 = stringToHex(inputBuf.substring(i + 5, i + 7), 2);
            mem[Controller.PCIndex++] = (byte) 0x95;
            mem[Controller.PCIndex++] = hexVal1;
            i += 9;
        }
        //STA Abs (STA $0FFF)
        else if (inputBuf.charAt(i + 4) == '$' && (inputBuf.charAt(i + 9) == '\n' || inputBuf.charAt(i + 9) == ' ' || inputBuf.charAt(i + 9) == '\0')) {
            System.out.println("in STA $0FFF");
            byte hexVal1 = stringToHex(inputBuf.substring(i + 5, i + 7), 2);
            byte hexVal2 = stringToHex(inputBuf.substring(i + 7, i + 9), 2);
            mem[Controller.PCIndex++] = (byte) 0x8D;
            mem[Controller.PCIndex++] = hexVal2;  //little endian order, least significant byte
            mem[Controller.PCIndex++] = hexVal1;  //little endian order, most significant byte
            i += 8;
        }
        //STA Abs, X (STA $0FFF, X)
        else if ( (inputBuf.charAt(i + 9) == ',' && inputBuf.charAt(i+11) == 'X') && (inputBuf.charAt(i + 12) == '\n' || inputBuf.charAt(i + 12) == ' ' || inputBuf.charAt(i + 12) == '\0')) {
            System.out.println("in STA Abs, X");
            //int hexVal1 = stringToHex(inputBuf.substring(i + 5, i + 8), 3);
            //String addVal = Integer.toHexString(hexVal1 + X.val);
            //hexVal1 = stringToHex(addVal.substring(0, 2), 2);
            //int hexVal2 = stringToHex(addVal.substring(2, 3), 1);

            byte hexVal1 = stringToHex(inputBuf.substring(i + 5, i + 7), 2);
            byte hexVal2 = stringToHex(inputBuf.substring(i + 7, i + 9), 2);

            mem[Controller.PCIndex++] = (byte) 0x9D;
            mem[Controller.PCIndex++] = hexVal2;  //little endian order, least significant byte
            mem[Controller.PCIndex++] = hexVal1;  //little endian order, most significant byte
            i += 11;   //changed from 10
        }
        //STA Abs, Y (STA $0FFF, Y)
        else if ((inputBuf.charAt(i+8) != ')' && inputBuf.charAt(i + 9) == ',' && inputBuf.charAt(i+11) == 'Y') && (inputBuf.charAt(i + 12) == '\n' || inputBuf.charAt(i + 12) == ' ' || inputBuf.charAt(i + 12) == '\0')) {
            System.out.println("in STA Abs, Y");
            //int hexVal1 = stringToHex(inputBuf.substring(i + 5, i + 8), 3);
            //String addVal = Integer.toHexString(hexVal1 + Y.val);
            //hexVal1 = stringToHex(addVal.substring(0, 2), 2);
            //int hexVal2 = stringToHex(addVal.substring(2, 3), 1);

            byte hexVal1 = stringToHex(inputBuf.substring(i + 5, i + 7), 2);
            byte hexVal2 = stringToHex(inputBuf.substring(i + 7, i + 9), 2);

            mem[Controller.PCIndex++] = (byte) 0x99;
            mem[Controller.PCIndex++] = hexVal2;  //little endian order, least significant byte
            mem[Controller.PCIndex++] = hexVal1;  //little endian order, most significant byte
            i += 11;   //changed from 10
        }
        //STA (ZP, X)  (STA ($FF, X)
        else if((inputBuf.charAt(i+4) == '(' && inputBuf.charAt(i+11) == ')') && (inputBuf.charAt(i+12) == '\n' || inputBuf.charAt(i+12) == ' ' || inputBuf.charAt(i+12) == '\0') ){
            System.out.println("in STA (ZP, X)");
            byte hexVal1 = stringToHex(inputBuf.substring(i+6,i+8), 2);

            mem[Controller.PCIndex++] = (byte) 0x81;
            mem[Controller.PCIndex++] = hexVal1;
            i+=11;
        }
        //STA (ZP), Y ( STA ($FF), Y )
        else if((inputBuf.charAt(i+4) == '(' && inputBuf.charAt(i+8) == ')') && (inputBuf.charAt(i+12) == '\n' || inputBuf.charAt(i+12) == ' ' || inputBuf.charAt(i+12) == '\0') ){
            System.out.println("in STA (ZP), Y");
            byte hexVal1 = stringToHex(inputBuf.substring(i+6,i+8), 2);

            mem[Controller.PCIndex++] = (byte) 0x91;
            mem[Controller.PCIndex++] = hexVal1;
            i+=11;
        }
        else{
            System.out.println("syntax error in child STA method: " + inputBuf.charAt(i) + ", " + i);
        }
        return i;
    }

    private static int instructionSTX(byte[] mem, String inputBuf, int i, Register Y){
        //STX ZP (STX $FF)
        if(inputBuf.charAt(i + 4) == '$' && (inputBuf.charAt(i + 7) == '\n' || inputBuf.charAt(i + 7) == ' ') || inputBuf.charAt(i + 7) == '\0') {
            System.out.println("in STX $FF");
            byte hexVal = stringToHex(inputBuf.substring(i + 5, i + 7), 2);
            mem[Controller.PCIndex++] = (byte) 0x86;
            mem[Controller.PCIndex++] = hexVal;
            i += 6;
        }
        //STX ZP, Y (STX $FF, Y)
        else if (inputBuf.charAt(i + 7) == ',' && (inputBuf.charAt(i + 10) == '\n' || inputBuf.charAt(i + 10) == ' ' || inputBuf.charAt(i + 10) == '\0')) {
            System.out.println("in STX ZP, Y");
            byte hexVal1 = stringToHex(inputBuf.substring(i + 5, i + 7), 2);
            mem[Controller.PCIndex++] = (byte) 0x96;
            mem[Controller.PCIndex++] = hexVal1;
            i += 9;
        }
        //STX Abs (STX $0FFF)
        else if (inputBuf.charAt(i + 4) == '$' && (inputBuf.charAt(i + 9) == '\n' || inputBuf.charAt(i + 9) == ' ' || inputBuf.charAt(i + 9) == '\0')) {
            System.out.println("in STX $0FFF");
            byte hexVal1 = stringToHex(inputBuf.substring(i + 5, i + 7), 2);
            byte hexVal2 = stringToHex(inputBuf.substring(i + 7, i + 9), 2);
            mem[Controller.PCIndex++] = (byte) 0x8E;
            mem[Controller.PCIndex++] = hexVal2;  //little endian order, least significant byte
            mem[Controller.PCIndex++] = hexVal1;  //little endian order, most significant byte
            i += 8;  //changed from 7
        }
        else{
            System.out.println("syntax error in child STX method: " + inputBuf.charAt(i) + ", " + i);
        }
        return i;
    }

    private static int instructionSTY(byte[] mem, String inputBuf, int i, Register X){
        //STY ZP (STY $FF)
        if(inputBuf.charAt(i + 4) == '$' && (inputBuf.charAt(i + 7) == '\n' || inputBuf.charAt(i + 7) == ' ') || inputBuf.charAt(i + 7) == '\0') {
            System.out.println("in STY $FF");
            byte hexVal = stringToHex(inputBuf.substring(i + 5, i + 7), 2);
            mem[Controller.PCIndex++] = (byte) 0x84;
            mem[Controller.PCIndex++] = hexVal;
            i += 6;
        }
        //STY ZP, X (STY $FF, X)
        else if (inputBuf.charAt(i + 7) == ',' && (inputBuf.charAt(i + 10) == '\n' || inputBuf.charAt(i + 10) == ' ' || inputBuf.charAt(i + 10) == '\0')) {
            System.out.println("in STY ZP, X");
            byte hexVal1 = stringToHex(inputBuf.substring(i + 5, i + 7), 2);
            mem[Controller.PCIndex++] = (byte) 0x94;
            mem[Controller.PCIndex++] = hexVal1;
            i += 9;
        }
        //STY Abs (STY $0FFF)
        else if (inputBuf.charAt(i + 4) == '$' && (inputBuf.charAt(i + 9) == '\n' || inputBuf.charAt(i + 9) == ' ' || inputBuf.charAt(i + 9) == '\0')) {
            System.out.println("in STY $0FFF");
            byte hexVal1 = stringToHex(inputBuf.substring(i + 5, i + 7), 2);
            byte hexVal2 = stringToHex(inputBuf.substring(i + 7, i + 9), 2);
            mem[Controller.PCIndex++] = (byte) 0x8C;
            mem[Controller.PCIndex++] = hexVal2;  //little endian order, least significant byte
            mem[Controller.PCIndex++] = hexVal1;  //little endian order, most significant byte
            i += 8;   //changed from 7
        }
        else{
            System.out.println("syntax error in child STY method: " + inputBuf.charAt(i) + ", " + i);
        }
        return i;
    }

    //add with carry to the accumulator
    private static int instructionADC(byte[] mem, String inputBuf, int i, Register X, Register Y){
        //ADC #$FF
        if(inputBuf.substring(i+4,i+6).equals("#$")){
            System.out.println("in ADC #$");
            byte hexVal = stringToHex(inputBuf.substring(i+6,i+8), 2);
            mem[Controller.PCIndex++] = (byte) 0x69;
            mem[Controller.PCIndex++] = hexVal;
            i+=7;
        }
        //ADC $ZP
        else if(inputBuf.charAt(i + 4) == '$' && (inputBuf.charAt(i + 7) == '\n' || inputBuf.charAt(i + 7) == ' ') || inputBuf.charAt(i + 7) == '\0') {
            System.out.println("in ADC $FF");
            byte hexVal = stringToHex(inputBuf.substring(i+5, i+7), 2);
            mem[Controller.PCIndex++] = (byte) 0x65;
            mem[Controller.PCIndex++] = hexVal;
            i+=6;
        }
        //ADC $ZP, X
        else if (inputBuf.charAt(i + 7) == ',' && (inputBuf.charAt(i + 10) == '\n' || inputBuf.charAt(i + 10) == ' ' || inputBuf.charAt(i + 10) == '\0')) {
            System.out.println("in ADC ZP, X");
            byte hexVal1 = stringToHex(inputBuf.substring(i+5, i+7), 2);
            mem[Controller.PCIndex++] = (byte) 0x75;
            mem[Controller.PCIndex++] = hexVal1;
            i+=9;
        }
        //ADC $FFFF
        else if (inputBuf.charAt(i + 4) == '$' && (inputBuf.charAt(i + 9) == '\n' || inputBuf.charAt(i + 9) == ' ' || inputBuf.charAt(i + 9) == '\0')) {
            System.out.println("in ADC $FFFF");
            byte hexVal1 = stringToHex(inputBuf.substring(i+5, i+7), 2);
            byte hexVal2 = stringToHex(inputBuf.substring(i+7, i+9), 2);
            mem[Controller.PCIndex++] = (byte) 0x6D;
            mem[Controller.PCIndex++] = hexVal2;  //little endian order, least significant byte
            mem[Controller.PCIndex++] = hexVal1;  //little endian order, most significant byte
            i+=8;   //changed from 7
        }
        //ADC $FFFF, X
        else if ( ((inputBuf.charAt(i+8) != ')' && inputBuf.charAt(i + 9) == ',' && inputBuf.charAt(i+11) == 'X') && (inputBuf.charAt(i + 12) == '\n' || inputBuf.charAt(i + 12) == ' ' || inputBuf.charAt(i + 12) == '\0'))) {
            System.out.println("in ADC Abs, X");
            byte hexVal1 = stringToHex(inputBuf.substring(i+5, i+7), 2);
            byte hexVal2 = stringToHex(inputBuf.substring(i+7, i+9), 2);

            mem[Controller.PCIndex++] = (byte) 0x7D;
            mem[Controller.PCIndex++] = hexVal2;  //little endian order, least significant byte
            mem[Controller.PCIndex++] = hexVal1;  //little endian order, most significant byte
            i+=11;
        }
        //ADC $FFFF, Y
        else if ((inputBuf.charAt(i+8) != ')' && inputBuf.charAt(i + 9) == ',' && inputBuf.charAt(i+11) == 'Y') && (inputBuf.charAt(i + 12) == '\n' || inputBuf.charAt(i + 12) == ' ' || inputBuf.charAt(i + 12) == '\0')) {
            System.out.println("in ADC Abs, Y");
            byte hexVal1 = stringToHex(inputBuf.substring(i+5, i+7), 2);
            byte hexVal2 = stringToHex(inputBuf.substring(i+7, i+9), 2);

            mem[Controller.PCIndex++] = (byte) 0x79;
            mem[Controller.PCIndex++] = hexVal2;  //little endian order, least significant byte
            mem[Controller.PCIndex++] = hexVal1;  //little endian order, most significant byte
            i+=11;
        }
        //ADC (ZP,X) ( ADC ($FF, X) )
        else if((inputBuf.charAt(i+4) == '(' && inputBuf.charAt(i+11) == ')') && (inputBuf.charAt(i+12) == '\n' || inputBuf.charAt(i+12) == ' ' || inputBuf.charAt(i+12) == '\0') ){
            System.out.println("in ADC (ZP, X)");
            byte hexVal1 = stringToHex(inputBuf.substring(i+6,i+8), 2);

            mem[Controller.PCIndex++] = (byte) 0x61;
            mem[Controller.PCIndex++] = hexVal1;
            i+=11;
        }
        //ADC (ZP), Y   ( ADC ($FF), Y )
        else if((inputBuf.charAt(i+4) == '(' && inputBuf.charAt(i+8) == ')') && (inputBuf.charAt(i+12) == '\n' || inputBuf.charAt(i+12) == ' ' || inputBuf.charAt(i+12) == '\0') ){
            System.out.println("in LDA (ZP), Y");
            byte hexVal1 = stringToHex(inputBuf.substring(i+6,i+8), 2);

            mem[Controller.PCIndex++] = (byte) 0x71;
            mem[Controller.PCIndex++] = hexVal1;
            i+=11;
        }
        else {
            System.out.println("syntax error in child ADC method: " + inputBuf.charAt(i) + ", " + i);
        }
        return i;
    }

    //logical AND
    private static int instructionAND(byte[] mem, String inputBuf, int i, Register X, Register Y){
        //AND #$FF
        if(inputBuf.substring(i+4,i+6).equals("#$")){
            System.out.println("in AND #$");
            byte hexVal = stringToHex(inputBuf.substring(i+6,i+8), 2);
            mem[Controller.PCIndex++] = (byte) 0x29;
            mem[Controller.PCIndex++] = hexVal;
            i+=7;
        }
        //AND $ZP
        else if(inputBuf.charAt(i + 4) == '$' && (inputBuf.charAt(i + 7) == '\n' || inputBuf.charAt(i + 7) == ' ') || inputBuf.charAt(i + 7) == '\0') {
            System.out.println("in AND $FF");
            byte hexVal = stringToHex(inputBuf.substring(i+5, i+7), 2);
            mem[Controller.PCIndex++] = (byte) 0x25;
            mem[Controller.PCIndex++] = hexVal;
            i+=6;
        }
        //AND $ZP, X
        else if (inputBuf.charAt(i + 7) == ',' && (inputBuf.charAt(i + 10) == '\n' || inputBuf.charAt(i + 10) == ' ' || inputBuf.charAt(i + 10) == '\0')) {
            System.out.println("in AND ZP, X");
            byte hexVal1 = stringToHex(inputBuf.substring(i+5, i+7), 2);
            mem[Controller.PCIndex++] = (byte) 0x35;
            mem[Controller.PCIndex++] = hexVal1;
            i+=9;
        }
        //AND $FFFF
        else if (inputBuf.charAt(i + 4) == '$' && (inputBuf.charAt(i + 9) == '\n' || inputBuf.charAt(i + 9) == ' ' || inputBuf.charAt(i + 9) == '\0')) {
            System.out.println("in AND $FFFF");
            byte hexVal1 = stringToHex(inputBuf.substring(i+5, i+7), 2);
            byte hexVal2 = stringToHex(inputBuf.substring(i+7, i+9), 2);
            mem[Controller.PCIndex++] = (byte) 0x2D;
            mem[Controller.PCIndex++] = hexVal2;  //little endian order, least significant byte
            mem[Controller.PCIndex++] = hexVal1;  //little endian order, most significant byte
            i+=8;   //changed from 7
        }
        //AND $FFFF, X
        else if ( ((inputBuf.charAt(i+8) != ')' && inputBuf.charAt(i + 9) == ',' && inputBuf.charAt(i+11) == 'X') && (inputBuf.charAt(i + 12) == '\n' || inputBuf.charAt(i + 12) == ' ' || inputBuf.charAt(i + 12) == '\0'))) {
            System.out.println("in AND Abs, X");
            byte hexVal1 = stringToHex(inputBuf.substring(i+5, i+7), 2);
            byte hexVal2 = stringToHex(inputBuf.substring(i+7, i+9), 2);

            mem[Controller.PCIndex++] = (byte) 0x3D;
            mem[Controller.PCIndex++] = hexVal2;  //little endian order, least significant byte
            mem[Controller.PCIndex++] = hexVal1;  //little endian order, most significant byte
            i+=11;
        }
        //AND $FFFF, Y
        else if ((inputBuf.charAt(i+8) != ')' && inputBuf.charAt(i + 9) == ',' && inputBuf.charAt(i+11) == 'Y') && (inputBuf.charAt(i + 12) == '\n' || inputBuf.charAt(i + 12) == ' ' || inputBuf.charAt(i + 12) == '\0')) {
            System.out.println("in AND Abs, Y");
            byte hexVal1 = stringToHex(inputBuf.substring(i+5, i+7), 2);
            byte hexVal2 = stringToHex(inputBuf.substring(i+7, i+9), 2);

            mem[Controller.PCIndex++] = (byte) 0x39;
            mem[Controller.PCIndex++] = hexVal2;  //little endian order, least significant byte
            mem[Controller.PCIndex++] = hexVal1;  //little endian order, most significant byte
            i+=11;
        }
        //AND (ZP,X) ( AND ($FF, X) )
        else if((inputBuf.charAt(i+4) == '(' && inputBuf.charAt(i+11) == ')') && (inputBuf.charAt(i+12) == '\n' || inputBuf.charAt(i+12) == ' ' || inputBuf.charAt(i+12) == '\0') ){
            System.out.println("in AND (ZP, X)");
            byte hexVal1 = stringToHex(inputBuf.substring(i+6,i+8), 2);

            mem[Controller.PCIndex++] = (byte) 0x21;
            mem[Controller.PCIndex++] = hexVal1;
            i+=11;
        }
        //AND (ZP), Y   ( AND ($FF), Y )
        else if((inputBuf.charAt(i+4) == '(' && inputBuf.charAt(i+8) == ')') && (inputBuf.charAt(i+12) == '\n' || inputBuf.charAt(i+12) == ' ' || inputBuf.charAt(i+12) == '\0') ){
            System.out.println("in AND (ZP), Y");
            byte hexVal1 = stringToHex(inputBuf.substring(i+6,i+8), 2);

            mem[Controller.PCIndex++] = (byte) 0x31;
            mem[Controller.PCIndex++] = hexVal1;
            i+=11;
        }
        else {
            System.out.println("syntax error in child AND method: " + inputBuf.charAt(i) + ", " + i);
        }
        return i;
    }

    //left shift by one
    private static int instructionASL(byte[] mem, String inputBuf, int i, Register X, Register Y){
        //ASL A
        if(inputBuf.charAt(i+4) == 'A'){
            System.out.println("in ASL A");
            mem[Controller.PCIndex++] = (byte) 0x0A;
            i+=4;
        }
        //ASL $FF
        else if(inputBuf.charAt(i + 4) == '$' && (inputBuf.charAt(i + 7) == '\n' || inputBuf.charAt(i + 7) == ' ') || inputBuf.charAt(i + 7) == '\0') {
            System.out.println("in ASL $FF");
            byte hexVal = stringToHex(inputBuf.substring(i+5, i+7), 2);
            mem[Controller.PCIndex++] = (byte) 0x06;
            mem[Controller.PCIndex++] = hexVal;
            i+=6;
        }
        //ASL $FF, X
        else if (inputBuf.charAt(i + 7) == ',' && (inputBuf.charAt(i + 10) == '\n' || inputBuf.charAt(i + 10) == ' ' || inputBuf.charAt(i + 10) == '\0')) {
            System.out.println("in ASL ZP, X");
            byte hexVal1 = stringToHex(inputBuf.substring(i+5, i+7), 2);
            mem[Controller.PCIndex++] = (byte) 0x16;
            mem[Controller.PCIndex++] = hexVal1;
            i+=9;
        }
        //ASL $FFFF
        else if (inputBuf.charAt(i + 4) == '$' && (inputBuf.charAt(i + 9) == '\n' || inputBuf.charAt(i + 9) == ' ' || inputBuf.charAt(i + 9) == '\0')) {
            System.out.println("in ASL $FFFF");
            byte hexVal1 = stringToHex(inputBuf.substring(i+5, i+7), 2);
            byte hexVal2 = stringToHex(inputBuf.substring(i+7, i+9), 2);
            mem[Controller.PCIndex++] = (byte) 0x0E;
            mem[Controller.PCIndex++] = hexVal2;  //little endian order, least significant byte
            mem[Controller.PCIndex++] = hexVal1;  //little endian order, most significant byte
            i+=8;   //changed from 7
        }
        //ASL $FFFF, X
        else if ( ((inputBuf.charAt(i+8) != ')' && inputBuf.charAt(i + 9) == ',' && inputBuf.charAt(i+11) == 'X') && (inputBuf.charAt(i + 12) == '\n' || inputBuf.charAt(i + 12) == ' ' || inputBuf.charAt(i + 12) == '\0'))) {
            System.out.println("in ASL Abs, X");
            byte hexVal1 = stringToHex(inputBuf.substring(i+5, i+7), 2);
            byte hexVal2 = stringToHex(inputBuf.substring(i+7, i+9), 2);

            mem[Controller.PCIndex++] = (byte) 0x1E;
            mem[Controller.PCIndex++] = hexVal2;  //little endian order, least significant byte
            mem[Controller.PCIndex++] = hexVal1;  //little endian order, most significant byte
            i+=11;
        }
        return i;
    }

    //BCC
    private static int instructionBCC(byte[] mem, String inputBuf, List<Label> labelList, int i, Register X, Register Y){
        System.out.println("in BCC label");
        int labelLength = getLabelLength(inputBuf, i);
        int addr = findLabelAddress(inputBuf.substring(i+4, i+4+labelLength), labelList);

        mem[Controller.PCIndex++] = (byte) 0x90;
        mem[Controller.PCIndex++] = addr;

        i += 3+labelLength; //is this value correct???
        return i;
    }
    //BCS
    private static int instructionBCS(byte[] mem, String inputBuf, List<Label> labelList, int i, Register X, Register Y){
        System.out.println("in BCS label");

        int labelLength = getLabelLength(inputBuf, i);
        int addr = findLabelAddress(inputBuf.substring(i+4, i+4+labelLength), labelList);

        mem[Controller.PCIndex++] = (byte) 0xB0;
        mem[Controller.PCIndex++] = addr;

        i += 3+labelLength;
        return i;
    }
    //BEQ
    private static int instructionBEQ(byte[] mem, String inputBuf, List<Label> labelList, int i, Register X, Register Y){
        System.out.println("in BEQ label");
        int labelLength = getLabelLength(inputBuf, i);
        int addr = findLabelAddress(inputBuf.substring(i+4, i+4+labelLength), labelList);

        mem[Controller.PCIndex++] = (byte) 0xF0;
        mem[Controller.PCIndex++] = addr;


        i += 3+labelLength;
        return i;
    }
    //BIT
    private static int instructionBIT(byte[] mem, String inputBuf, int i, Register X, Register Y){
        System.out.println("in BIT");
        //BIT $FF
        if(inputBuf.charAt(i + 4) == '$' && (inputBuf.charAt(i + 7) == '\n' || inputBuf.charAt(i + 7) == ' ') || inputBuf.charAt(i + 7) == '\0') {
            System.out.println("in BIT $FF");
            byte hexVal = stringToHex(inputBuf.substring(i+5, i+7), 2);
            mem[Controller.PCIndex++] = (byte) 0x24;
            mem[Controller.PCIndex++] = hexVal;
            i+=6;
        }
        //BIT $FFFF
        else if (inputBuf.charAt(i + 4) == '$' && (inputBuf.charAt(i + 9) == '\n' || inputBuf.charAt(i + 9) == ' ' || inputBuf.charAt(i + 9) == '\0')) {
            System.out.println("in BIT $FFFF");
            byte hexVal1 = stringToHex(inputBuf.substring(i+5, i+7), 2);
            byte hexVal2 = stringToHex(inputBuf.substring(i+7, i+9), 2);
            mem[Controller.PCIndex++] = (byte) 0x2C;
            mem[Controller.PCIndex++] = hexVal2;  //little endian order, least significant byte
            mem[Controller.PCIndex++] = hexVal1;  //little endian order, most significant byte
            i+=8;
        }
        return i;
    }
    //BMI
    private static int instructionBMI(byte[] mem, String inputBuf, List<Label> labelList, int i, Register X, Register Y){
        System.out.println("in BMI label");
        int labelLength = getLabelLength(inputBuf, i);
        int addr = findLabelAddress(inputBuf.substring(i+4, i+4+labelLength), labelList);

        mem[Controller.PCIndex++] = (byte) 0x30;
        mem[Controller.PCIndex++] = addr;

        i += 3+labelLength;
        return i;
    }
    //BNE
    private static int instructionBNE(byte[] mem, String inputBuf, List<Label> labelList, int i, Register X, Register Y){
        System.out.println("in BNE label");
        int labelLength = getLabelLength(inputBuf, i);
        int addr = findLabelAddress(inputBuf.substring(i+4, i+4+labelLength), labelList);

        mem[Controller.PCIndex++] = (byte) 0xD0;
        mem[Controller.PCIndex++] = addr;

        i += 3+labelLength;
        return i;
    }
    //BPL
    private static int instructionBPL(byte[] mem, String inputBuf, List<Label> labelList, int i, Register X, Register Y){
        System.out.println("in BPL label");
        int labelLength = getLabelLength(inputBuf, i);
        int addr = findLabelAddress(inputBuf.substring(i+4, i+4+labelLength), labelList);

        mem[Controller.PCIndex++] = (byte) 0x10;
        mem[Controller.PCIndex++] = addr;

        i += 3+labelLength;
        return i;
    }
    //BVC
    private static int instructionBVC(byte[] mem, String inputBuf, List<Label> labelList, int i, Register X, Register Y){
        System.out.println("in BVC label");
        int labelLength = getLabelLength(inputBuf, i);
        int addr = findLabelAddress(inputBuf.substring(i+4, i+4+labelLength), labelList);

        mem[Controller.PCIndex++] = (byte) 0x50;
        mem[Controller.PCIndex++] = addr;

        i += 3+labelLength;
        return i;
    }
    //BVS
    private static int instructionBVS(byte[] mem, String inputBuf, List<Label> labelList, int i, Register X, Register Y){
        System.out.println("in BVS label");
        int labelLength = getLabelLength(inputBuf, i);
        int addr = findLabelAddress(inputBuf.substring(i+4, i+4+labelLength), labelList);

        mem[Controller.PCIndex++] = (byte) 0x70;
        mem[Controller.PCIndex++] = addr;

        i += 3+labelLength;
        return i;
    }
    //BRK
    private static int instructionBRK(byte[] mem, String inputBuf, List<Label> labelList, int i, Register X, Register Y){
        System.out.println("in BRK");
        mem[Controller.PCIndex++] = (byte) 0x00;

        i += 3;
        return i;
    }
    //CLC
    private static int instructionCLC(byte[] mem, String inputBuf, List<Label> labelList, int i, Register X, Register Y){
        System.out.println("in CLC");
        mem[Controller.PCIndex++] = (byte) 0x18;

        i += 3;
        return i;
    }
    //CLD
    private static int instructionCLD(byte[] mem, String inputBuf, List<Label> labelList, int i, Register X, Register Y){
        System.out.println("in CLD");
        mem[Controller.PCIndex++] = (byte) 0xD8;

        i += 3;
        return i;
    }
    //CLI
    private static int instructionCLI(byte[] mem, String inputBuf, List<Label> labelList, int i, Register X, Register Y){
        System.out.println("in CLI");
        mem[Controller.PCIndex++] = (byte) 0x58;

        i += 3;
        return i;
    }
    //CLV
    private static int instructionCLV(byte[] mem, String inputBuf, List<Label> labelList, int i, Register X, Register Y){
        System.out.println("in CLV");
        mem[Controller.PCIndex++] = (byte) 0xB8;

        i += 3;
        return i;
    }
    //SEC
    private static int instructionSEC(byte[] mem, String inputBuf, List<Label> labelList, int i, Register X, Register Y){
        System.out.println("in SEC");
        mem[Controller.PCIndex++] = (byte) 0x38;

        i += 3;
        return i;
    }
    //SED
    private static int instructionSED(byte[] mem, String inputBuf, List<Label> labelList, int i, Register X, Register Y){
        System.out.println("in SED");
        mem[Controller.PCIndex++] = (byte) 0xF8;

        i += 3;
        return i;
    }
    //SEI
    private static int instructionSEI(byte[] mem, String inputBuf, List<Label> labelList, int i, Register X, Register Y){
        System.out.println("in SEI");
        mem[Controller.PCIndex++] = (byte) 0x78;

        i += 3;
        return i;
    }

    //CMP
    private static int instructionCMP(byte[] mem, String inputBuf, int i, Register X, Register Y){
        //CMP #$FF
        if(inputBuf.substring(i+4,i+6).equals("#$")){
            System.out.println("in CMP #$");
            byte hexVal = stringToHex(inputBuf.substring(i+6,i+8), 2);
            mem[Controller.PCIndex++] = (byte) 0xC9;
            mem[Controller.PCIndex++] = hexVal;
            i+=7;
        }
        //CMP $ZP
        else if(inputBuf.charAt(i + 4) == '$' && (inputBuf.charAt(i + 7) == '\n' || inputBuf.charAt(i + 7) == ' ') || inputBuf.charAt(i + 7) == '\0') {
            System.out.println("in CMP $FF");
            byte hexVal = stringToHex(inputBuf.substring(i+5, i+7), 2);
            mem[Controller.PCIndex++] = (byte) 0xC5;
            mem[Controller.PCIndex++] = hexVal;
            i+=6;
        }
        //CMP $ZP, X
        else if (inputBuf.charAt(i + 7) == ',' && (inputBuf.charAt(i + 10) == '\n' || inputBuf.charAt(i + 10) == ' ' || inputBuf.charAt(i + 10) == '\0')) {
            System.out.println("in CMP ZP, X");
            byte hexVal1 = stringToHex(inputBuf.substring(i+5, i+7), 2);
            mem[Controller.PCIndex++] = (byte) 0xD5;
            mem[Controller.PCIndex++] = hexVal1;
            i+=9;
        }
        //CMP $FFFF
        else if (inputBuf.charAt(i + 4) == '$' && (inputBuf.charAt(i + 9) == '\n' || inputBuf.charAt(i + 9) == ' ' || inputBuf.charAt(i + 9) == '\0')) {
            System.out.println("in CMP $FFFF");
            byte hexVal1 = stringToHex(inputBuf.substring(i+5, i+7), 2);
            byte hexVal2 = stringToHex(inputBuf.substring(i+7, i+9), 2);
            mem[Controller.PCIndex++] = (byte) 0xCD;
            mem[Controller.PCIndex++] = hexVal2;  //little endian order, least significant byte
            mem[Controller.PCIndex++] = hexVal1;  //little endian order, most significant byte
            i+=8;   //changed from 7
        }
        //CMP $FFFF, X
        else if ( ((inputBuf.charAt(i+8) != ')' && inputBuf.charAt(i + 9) == ',' && inputBuf.charAt(i+11) == 'X') && (inputBuf.charAt(i + 12) == '\n' || inputBuf.charAt(i + 12) == ' ' || inputBuf.charAt(i + 12) == '\0'))) {
            System.out.println("in CMP Abs, X");
            byte hexVal1 = stringToHex(inputBuf.substring(i+5, i+7), 2);
            byte hexVal2 = stringToHex(inputBuf.substring(i+7, i+9), 2);
            mem[Controller.PCIndex++] = (byte) 0xDD;
            mem[Controller.PCIndex++] = hexVal2;  //little endian order, least significant byte
            mem[Controller.PCIndex++] = hexVal1;  //little endian order, most significant byte
            i+=11;
        }
        //CMP $FFFF, Y
        else if ((inputBuf.charAt(i+8) != ')' && inputBuf.charAt(i + 9) == ',' && inputBuf.charAt(i+11) == 'Y') && (inputBuf.charAt(i + 12) == '\n' || inputBuf.charAt(i + 12) == ' ' || inputBuf.charAt(i + 12) == '\0')) {
            System.out.println("in CMP Abs, Y");
            byte hexVal1 = stringToHex(inputBuf.substring(i+5, i+7), 2);
            byte hexVal2 = stringToHex(inputBuf.substring(i+7, i+9), 2);
            mem[Controller.PCIndex++] = (byte) 0xD9;
            mem[Controller.PCIndex++] = hexVal2;  //little endian order, least significant byte
            mem[Controller.PCIndex++] = hexVal1;  //little endian order, most significant byte
            i+=11;
        }
        //CMP (ZP,X) ( CMP ($FF, X) )
        else if((inputBuf.charAt(i+4) == '(' && inputBuf.charAt(i+11) == ')') && (inputBuf.charAt(i+12) == '\n' || inputBuf.charAt(i+12) == ' ' || inputBuf.charAt(i+12) == '\0') ){
            System.out.println("in CMP (ZP, X)");
            byte hexVal1 = stringToHex(inputBuf.substring(i+6,i+8), 2);
            mem[Controller.PCIndex++] = (byte) 0xC1;
            mem[Controller.PCIndex++] = hexVal1;
            i+=11;
        }
        //CMP (ZP), Y   ( CMP ($FF), Y )
        else if((inputBuf.charAt(i+4) == '(' && inputBuf.charAt(i+8) == ')') && (inputBuf.charAt(i+12) == '\n' || inputBuf.charAt(i+12) == ' ' || inputBuf.charAt(i+12) == '\0') ){
            System.out.println("in CMP (ZP), Y");
            byte hexVal1 = stringToHex(inputBuf.substring(i+6,i+8), 2);
            mem[Controller.PCIndex++] = (byte) 0xD1;
            mem[Controller.PCIndex++] = hexVal1;
            i+=11;
        }
        else {
            System.out.println("syntax error in child CMP method: " + inputBuf.charAt(i) + ", " + i);
        }
        return i;
    }

    //CPX
    private static int instructionCPX(byte[] mem, String inputBuf, int i, Register X, Register Y) {
        //CPX #$FF
        if (inputBuf.substring(i + 4, i + 6).equals("#$")) {
            System.out.println("in CPX #$");
            byte hexVal = stringToHex(inputBuf.substring(i + 6, i + 8), 2);
            mem[Controller.PCIndex++] = (byte) 0xE0;
            mem[Controller.PCIndex++] = hexVal;
            i += 7;
        }
        //CPX $ZP
        else if (inputBuf.charAt(i + 4) == '$' && (inputBuf.charAt(i + 7) == '\n' || inputBuf.charAt(i + 7) == ' ') || inputBuf.charAt(i + 7) == '\0') {
            System.out.println("in CPX $FF");
            byte hexVal = stringToHex(inputBuf.substring(i + 5, i + 7), 2);
            mem[Controller.PCIndex++] = (byte) 0xE4;
            mem[Controller.PCIndex++] = hexVal;
            i += 6;
        }
        //CPX $FFFF
        else if (inputBuf.charAt(i + 4) == '$' && (inputBuf.charAt(i + 9) == '\n' || inputBuf.charAt(i + 9) == ' ' || inputBuf.charAt(i + 9) == '\0')) {
            System.out.println("in CPX $FFFF");
            byte hexVal1 = stringToHex(inputBuf.substring(i + 5, i + 7), 2);
            byte hexVal2 = stringToHex(inputBuf.substring(i + 7, i + 9), 2);
            mem[Controller.PCIndex++] = (byte) 0xEC;
            mem[Controller.PCIndex++] = hexVal2;  //little endian order, least significant byte
            mem[Controller.PCIndex++] = hexVal1;  //little endian order, most significant byte
            i += 8;   //changed from 7
        }
        else {
            System.out.println("syntax error in child CPX method: " + inputBuf.charAt(i) + ", " + i);
        }
        return i;
    }

    //CPY
    private static int instructionCPY(byte[] mem, String inputBuf, int i, Register X, Register Y) {
        //CPY #$FF
        if (inputBuf.substring(i + 4, i + 6).equals("#$")) {
            System.out.println("in CPY #$");
            byte hexVal = stringToHex(inputBuf.substring(i + 6, i + 8), 2);
            mem[Controller.PCIndex++] = (byte) 0xC0;
            mem[Controller.PCIndex++] = hexVal;
            i += 7;
        }
        //CPY $ZP
        else if (inputBuf.charAt(i + 4) == '$' && (inputBuf.charAt(i + 7) == '\n' || inputBuf.charAt(i + 7) == ' ') || inputBuf.charAt(i + 7) == '\0') {
            System.out.println("in CPY $FF");
            byte hexVal = stringToHex(inputBuf.substring(i + 5, i + 7), 2);
            mem[Controller.PCIndex++] = (byte) 0xC4;
            mem[Controller.PCIndex++] = hexVal;
            i += 6;
        }
        //CPY $FFFF
        else if (inputBuf.charAt(i + 4) == '$' && (inputBuf.charAt(i + 9) == '\n' || inputBuf.charAt(i + 9) == ' ' || inputBuf.charAt(i + 9) == '\0')) {
            System.out.println("in CPY $FFFF");
            byte hexVal1 = stringToHex(inputBuf.substring(i + 5, i + 7), 2);
            byte hexVal2 = stringToHex(inputBuf.substring(i + 7, i + 9), 2);
            mem[Controller.PCIndex++] = (byte) 0xCC;
            mem[Controller.PCIndex++] = hexVal2;  //little endian order, least significant byte
            mem[Controller.PCIndex++] = hexVal1;  //little endian order, most significant byte
            i += 8;   //changed from 7
        }
        else {
            System.out.println("syntax error in child CPY method: " + inputBuf.charAt(i) + ", " + i);
        }
        return i;
    }

    //DEC
    private static int instructionDEC(byte[] mem, String inputBuf, int i, Register X, Register Y){
        //DEC $ZP
        if(inputBuf.charAt(i + 4) == '$' && (inputBuf.charAt(i + 7) == '\n' || inputBuf.charAt(i + 7) == ' ') || inputBuf.charAt(i + 7) == '\0') {
            System.out.println("in DEC $FF");
            byte hexVal = stringToHex(inputBuf.substring(i+5, i+7), 2);
            mem[Controller.PCIndex++] = (byte) 0xC6;
            mem[Controller.PCIndex++] = hexVal;
            i+=6;
        }
        //DEC $ZP, X
        else if (inputBuf.charAt(i + 7) == ',' && (inputBuf.charAt(i + 10) == '\n' || inputBuf.charAt(i + 10) == ' ' || inputBuf.charAt(i + 10) == '\0')) {
            System.out.println("in DEC ZP, X");
            byte hexVal1 = stringToHex(inputBuf.substring(i+5, i+7), 2);
            mem[Controller.PCIndex++] = (byte) 0xD6;
            mem[Controller.PCIndex++] = hexVal1;
            i+=9;
        }
        //DEC $FFFF
        else if (inputBuf.charAt(i + 4) == '$' && (inputBuf.charAt(i + 9) == '\n' || inputBuf.charAt(i + 9) == ' ' || inputBuf.charAt(i + 9) == '\0')) {
            System.out.println("in DEC $FFFF");
            byte hexVal1 = stringToHex(inputBuf.substring(i+5, i+7), 2);
            byte hexVal2 = stringToHex(inputBuf.substring(i+7, i+9), 2);
            mem[Controller.PCIndex++] = (byte) 0xCE;
            mem[Controller.PCIndex++] = hexVal2;  //little endian order, least significant byte
            mem[Controller.PCIndex++] = hexVal1;  //little endian order, most significant byte
            i+=8;   //changed from 7
        }
        //DEC $FFFF, X
        else if ( ((inputBuf.charAt(i+8) != ')' && inputBuf.charAt(i + 9) == ',' && inputBuf.charAt(i+11) == 'X') && (inputBuf.charAt(i + 12) == '\n' || inputBuf.charAt(i + 12) == ' ' || inputBuf.charAt(i + 12) == '\0'))) {
            System.out.println("in DEC Abs, X");
            byte hexVal1 = stringToHex(inputBuf.substring(i+5, i+7), 2);
            byte hexVal2 = stringToHex(inputBuf.substring(i+7, i+9), 2);

            mem[Controller.PCIndex++] = (byte) 0xDE;
            mem[Controller.PCIndex++] = hexVal2;  //little endian order, least significant byte
            mem[Controller.PCIndex++] = hexVal1;  //little endian order, most significant byte
            i+=11;
        }
        else {
            System.out.println("syntax error in child DEC method: " + inputBuf.charAt(i) + ", " + i);
        }
        return i;
    }

    //DEX
    private static int instructionDEX(byte[] mem, String inputBuf, int i, Register A, Register X){
        System.out.println("in DEX");
        mem[Controller.PCIndex++] = (byte) 0xCA;
        i+=3;
        return i;
    }

    //DEY
    private static int instructionDEY(byte[] mem, String inputBuf, int i, Register A, Register X){
        System.out.println("in DEY");
        mem[Controller.PCIndex++] = (byte) 0x88;
        i+=3;
        return i;
    }


    //EOR
    private static int instructionEOR(byte[] mem, String inputBuf, int i, Register X, Register Y){
        //EOR #$FF
        if(inputBuf.substring(i+4,i+6).equals("#$")){
            System.out.println("in EOR #$");
            byte hexVal = stringToHex(inputBuf.substring(i+6,i+8), 2);
            mem[Controller.PCIndex++] = (byte) 0x49;
            mem[Controller.PCIndex++] = hexVal;
            i+=7;
        }
        //EOR $ZP
        else if(inputBuf.charAt(i + 4) == '$' && (inputBuf.charAt(i + 7) == '\n' || inputBuf.charAt(i + 7) == ' ') || inputBuf.charAt(i + 7) == '\0') {
            System.out.println("in EOR $FF");
            byte hexVal = stringToHex(inputBuf.substring(i+5, i+7), 2);
            mem[Controller.PCIndex++] = (byte) 0x45;
            mem[Controller.PCIndex++] = hexVal;
            i+=6;
        }
        //EOR $ZP, X
        else if (inputBuf.charAt(i + 7) == ',' && (inputBuf.charAt(i + 10) == '\n' || inputBuf.charAt(i + 10) == ' ' || inputBuf.charAt(i + 10) == '\0')) {
            System.out.println("in EOR ZP, X");
            byte hexVal1 = stringToHex(inputBuf.substring(i+5, i+7), 2);
            mem[Controller.PCIndex++] = (byte) 0x55;
            mem[Controller.PCIndex++] = hexVal1;
            i+=9;
        }
        //EOR $FFFF
        else if (inputBuf.charAt(i + 4) == '$' && (inputBuf.charAt(i + 9) == '\n' || inputBuf.charAt(i + 9) == ' ' || inputBuf.charAt(i + 9) == '\0')) {
            System.out.println("in EOR $FFFF");
            byte hexVal1 = stringToHex(inputBuf.substring(i+5, i+7), 2);
            byte hexVal2 = stringToHex(inputBuf.substring(i+7, i+9), 2);
            mem[Controller.PCIndex++] = (byte) 0x4D;
            mem[Controller.PCIndex++] = hexVal2;  //little endian order, least significant byte
            mem[Controller.PCIndex++] = hexVal1;  //little endian order, most significant byte
            i+=8;   //changed from 7
        }
        //EOR $FFFF, X
        else if ( ((inputBuf.charAt(i+8) != ')' && inputBuf.charAt(i + 9) == ',' && inputBuf.charAt(i+11) == 'X') && (inputBuf.charAt(i + 12) == '\n' || inputBuf.charAt(i + 12) == ' ' || inputBuf.charAt(i + 12) == '\0'))) {
            System.out.println("in EOR Abs, X");
            byte hexVal1 = stringToHex(inputBuf.substring(i+5, i+7), 2);
            byte hexVal2 = stringToHex(inputBuf.substring(i+7, i+9), 2);
            mem[Controller.PCIndex++] = (byte) 0x5D;
            mem[Controller.PCIndex++] = hexVal2;  //little endian order, least significant byte
            mem[Controller.PCIndex++] = hexVal1;  //little endian order, most significant byte
            i+=11;
        }
        //EOR $FFFF, Y
        else if ((inputBuf.charAt(i+8) != ')' && inputBuf.charAt(i + 9) == ',' && inputBuf.charAt(i+11) == 'Y') && (inputBuf.charAt(i + 12) == '\n' || inputBuf.charAt(i + 12) == ' ' || inputBuf.charAt(i + 12) == '\0')) {
            System.out.println("in EOR Abs, Y");
            byte hexVal1 = stringToHex(inputBuf.substring(i+5, i+7), 2);
            byte hexVal2 = stringToHex(inputBuf.substring(i+7, i+9), 2);
            mem[Controller.PCIndex++] = (byte) 0x59;
            mem[Controller.PCIndex++] = hexVal2;  //little endian order, least significant byte
            mem[Controller.PCIndex++] = hexVal1;  //little endian order, most significant byte
            i+=11;
        }
        //EOR (ZP,X) ( CMP ($FF, X) )
        else if((inputBuf.charAt(i+4) == '(' && inputBuf.charAt(i+11) == ')') && (inputBuf.charAt(i+12) == '\n' || inputBuf.charAt(i+12) == ' ' || inputBuf.charAt(i+12) == '\0') ){
            System.out.println("in EOR (ZP, X)");
            byte hexVal1 = stringToHex(inputBuf.substring(i+6,i+8), 2);
            mem[Controller.PCIndex++] = (byte) 0x41;
            mem[Controller.PCIndex++] = hexVal1;
            i+=11;
        }
        //EOR (ZP), Y   ( CMP ($FF), Y )
        else if((inputBuf.charAt(i+4) == '(' && inputBuf.charAt(i+8) == ')') && (inputBuf.charAt(i+12) == '\n' || inputBuf.charAt(i+12) == ' ' || inputBuf.charAt(i+12) == '\0') ){
            System.out.println("in EOR (ZP), Y");
            byte hexVal1 = stringToHex(inputBuf.substring(i+6,i+8), 2);
            mem[Controller.PCIndex++] = (byte) 0x51;
            mem[Controller.PCIndex++] = hexVal1;
            i+=11;
        }
        else {
            System.out.println("syntax error in child EOR method: " + inputBuf.charAt(i) + ", " + i);
        }
        return i;
    }

    //INC
    private static int instructionINC(byte[] mem, String inputBuf, int i, Register X, Register Y){
        //INC $ZP
        if(inputBuf.charAt(i + 4) == '$' && (inputBuf.charAt(i + 7) == '\n' || inputBuf.charAt(i + 7) == ' ') || inputBuf.charAt(i + 7) == '\0') {
            System.out.println("in INC $FF");
            byte hexVal = stringToHex(inputBuf.substring(i+5, i+7), 2);
            mem[Controller.PCIndex++] = (byte) 0xE6;
            mem[Controller.PCIndex++] = hexVal;
            i+=6;
        }
        //INC $ZP, X
        else if (inputBuf.charAt(i + 7) == ',' && (inputBuf.charAt(i + 10) == '\n' || inputBuf.charAt(i + 10) == ' ' || inputBuf.charAt(i + 10) == '\0')) {
            System.out.println("in INC ZP, X");
            byte hexVal1 = stringToHex(inputBuf.substring(i+5, i+7), 2);
            mem[Controller.PCIndex++] = (byte) 0xF6;
            mem[Controller.PCIndex++] = hexVal1;
            i+=9;
        }
        //INC $FFFF
        else if (inputBuf.charAt(i + 4) == '$' && (inputBuf.charAt(i + 9) == '\n' || inputBuf.charAt(i + 9) == ' ' || inputBuf.charAt(i + 9) == '\0')) {
            System.out.println("in INC $FFFF");
            byte hexVal1 = stringToHex(inputBuf.substring(i+5, i+7), 2);
            byte hexVal2 = stringToHex(inputBuf.substring(i+7, i+9), 2);
            mem[Controller.PCIndex++] = (byte) 0xEE;
            mem[Controller.PCIndex++] = hexVal2;  //little endian order, least significant byte
            mem[Controller.PCIndex++] = hexVal1;  //little endian order, most significant byte
            i+=8;   //changed from 7
        }
        //INC $FFFF, X
        else if ( ((inputBuf.charAt(i+8) != ')' && inputBuf.charAt(i + 9) == ',' && inputBuf.charAt(i+11) == 'X') && (inputBuf.charAt(i + 12) == '\n' || inputBuf.charAt(i + 12) == ' ' || inputBuf.charAt(i + 12) == '\0'))) {
            System.out.println("in INC Abs, X");
            byte hexVal1 = stringToHex(inputBuf.substring(i+5, i+7), 2);
            byte hexVal2 = stringToHex(inputBuf.substring(i+7, i+9), 2);

            mem[Controller.PCIndex++] = (byte) 0xFE;
            mem[Controller.PCIndex++] = hexVal2;  //little endian order, least significant byte
            mem[Controller.PCIndex++] = hexVal1;  //little endian order, most significant byte
            i+=11;
        }
        else {
            System.out.println("syntax error in child INC method: " + inputBuf.charAt(i) + ", " + i);
        }
        return i;
    }

    //INX
    private static int instructionINX(byte[] mem, String inputBuf, int i, Register A, Register X){
        System.out.println("in INX");
        mem[Controller.PCIndex++] = (byte) 0xE8;
        i+=3;
        return i;
    }

    //INY
    private static int instructionINY(byte[] mem, String inputBuf, int i, Register A, Register X){
        System.out.println("in INY");
        mem[Controller.PCIndex++] = (byte) 0xC8;
        i+=3;
        return i;
    }

    //JMP
    private static int instructionJMP(byte[] mem, String inputBuf, List<Label> labelList, int i, Register X, Register Y){
        int labelLength=0;
        int addr=0;

        //JMP (label)
        if(inputBuf.charAt(i+4) == '('){
            System.out.println("in JMP (label)");
            labelLength = getLabelLength(inputBuf, i);
            addr = findLabelAddress(inputBuf.substring(i + 4, i + 4 + labelLength), labelList);

            mem[Controller.PCIndex++] = (byte) 0x6C;
            mem[Controller.PCIndex++] = addr;
        }
        //JMP label
        else{
            System.out.println("in JMP label");
            labelLength = getLabelLength(inputBuf, i);
            addr = findLabelAddress(inputBuf.substring(i + 4, i + 4 + labelLength), labelList);

            mem[Controller.PCIndex++] = (byte) 0x4C;
            mem[Controller.PCIndex++] = addr;
        }

        i += 3+labelLength; //is this value correct???
        return i;
    }

    //JSR label
    private static int instructionJSR(byte[] mem, String inputBuf, List<Label> labelList, int i, Register X, Register Y){
        System.out.println("in JSR label");
        int labelLength = getLabelLength(inputBuf, i);
        int addr = findLabelAddress(inputBuf.substring(i + 4, i + 4 + labelLength), labelList);
        mem[Controller.PCIndex++] = (byte) 0x20;
        mem[Controller.PCIndex++] = addr;

        i += 3+labelLength; //is this value correct???
        return i;
    }

    //right shift by one
    private static int instructionLSR(byte[] mem, String inputBuf, int i, Register X, Register Y){
        //LSR A
        if(inputBuf.charAt(i+4) == 'A'){
            System.out.println("in LSR A");
            mem[Controller.PCIndex++] = (byte) 0x4A;
            i+=4;
        }
        //LSR $FF
        else if(inputBuf.charAt(i + 4) == '$' && (inputBuf.charAt(i + 7) == '\n' || inputBuf.charAt(i + 7) == ' ') || inputBuf.charAt(i + 7) == '\0') {
            System.out.println("in LSR $FF");
            byte hexVal = stringToHex(inputBuf.substring(i+5, i+7), 2);
            mem[Controller.PCIndex++] = (byte) 0x46;
            mem[Controller.PCIndex++] = hexVal;
            i+=6;
        }
        //LSR $FF, X
        else if (inputBuf.charAt(i + 7) == ',' && (inputBuf.charAt(i + 10) == '\n' || inputBuf.charAt(i + 10) == ' ' || inputBuf.charAt(i + 10) == '\0')) {
            System.out.println("in LSR ZP, X");
            byte hexVal1 = stringToHex(inputBuf.substring(i+5, i+7), 2);
            mem[Controller.PCIndex++] = (byte) 0x56;
            mem[Controller.PCIndex++] = hexVal1;
            i+=9;
        }
        //LSR $FFFF
        else if (inputBuf.charAt(i + 4) == '$' && (inputBuf.charAt(i + 9) == '\n' || inputBuf.charAt(i + 9) == ' ' || inputBuf.charAt(i + 9) == '\0')) {
            System.out.println("in LSR $FFFF");
            byte hexVal1 = stringToHex(inputBuf.substring(i+5, i+7), 2);
            byte hexVal2 = stringToHex(inputBuf.substring(i+7, i+9), 2);
            mem[Controller.PCIndex++] = (byte) 0x4E;
            mem[Controller.PCIndex++] = hexVal2;  //little endian order, least significant byte
            mem[Controller.PCIndex++] = hexVal1;  //little endian order, most significant byte
            i+=8;   //changed from 7
        }
        //LSR $FFFF, X
        else if ( ((inputBuf.charAt(i+8) != ')' && inputBuf.charAt(i + 9) == ',' && inputBuf.charAt(i+11) == 'X') && (inputBuf.charAt(i + 12) == '\n' || inputBuf.charAt(i + 12) == ' ' || inputBuf.charAt(i + 12) == '\0'))) {
            System.out.println("in LSR Abs, X");
            byte hexVal1 = stringToHex(inputBuf.substring(i+5, i+7), 2);
            byte hexVal2 = stringToHex(inputBuf.substring(i+7, i+9), 2);

            mem[Controller.PCIndex++] = (byte) 0x5E;
            mem[Controller.PCIndex++] = hexVal2;  //little endian order, least significant byte
            mem[Controller.PCIndex++] = hexVal1;  //little endian order, most significant byte
            i+=11;
        }
        return i;
    }

    //NOP
    private static int instructionNOP(byte[] mem, String inputBuf, int i, Register X, Register Y){
        System.out.println("in NOP");
        mem[Controller.PCIndex++] = (byte) 0xEA;

        i += 3;
        return i;
    }

    //logical OR
    private static int instructionORA(byte[] mem, String inputBuf, int i, Register X, Register Y){
        //ORA #$FF
        if(inputBuf.substring(i+4,i+6).equals("#$")){
            System.out.println("in ORA #$");
            byte hexVal = stringToHex(inputBuf.substring(i+6,i+8), 2);
            mem[Controller.PCIndex++] = (byte) 0x09;
            mem[Controller.PCIndex++] = hexVal;
            i+=7;
        }
        //ORA $ZP
        else if(inputBuf.charAt(i + 4) == '$' && (inputBuf.charAt(i + 7) == '\n' || inputBuf.charAt(i + 7) == ' ') || inputBuf.charAt(i + 7) == '\0') {
            System.out.println("in ORA $FF");
            byte hexVal = stringToHex(inputBuf.substring(i+5, i+7), 2);
            mem[Controller.PCIndex++] = (byte) 0x05;
            mem[Controller.PCIndex++] = hexVal;
            i+=6;
        }
        //ORA $ZP, X
        else if (inputBuf.charAt(i + 7) == ',' && (inputBuf.charAt(i + 10) == '\n' || inputBuf.charAt(i + 10) == ' ' || inputBuf.charAt(i + 10) == '\0')) {
            System.out.println("in ORA ZP, X");
            byte hexVal1 = stringToHex(inputBuf.substring(i+5, i+7), 2);
            mem[Controller.PCIndex++] = (byte) 0x15;
            mem[Controller.PCIndex++] = hexVal1;
            i+=9;
        }
        //ORA $FFFF
        else if (inputBuf.charAt(i + 4) == '$' && (inputBuf.charAt(i + 9) == '\n' || inputBuf.charAt(i + 9) == ' ' || inputBuf.charAt(i + 9) == '\0')) {
            System.out.println("in ORA $FFFF");
            byte hexVal1 = stringToHex(inputBuf.substring(i+5, i+7), 2);
            byte hexVal2 = stringToHex(inputBuf.substring(i+7, i+9), 2);
            mem[Controller.PCIndex++] = (byte) 0x0D;
            mem[Controller.PCIndex++] = hexVal2;  //little endian order, least significant byte
            mem[Controller.PCIndex++] = hexVal1;  //little endian order, most significant byte
            i+=8;   //changed from 7
        }
        //ORA $FFFF, X
        else if ( ((inputBuf.charAt(i+8) != ')' && inputBuf.charAt(i + 9) == ',' && inputBuf.charAt(i+11) == 'X') && (inputBuf.charAt(i + 12) == '\n' || inputBuf.charAt(i + 12) == ' ' || inputBuf.charAt(i + 12) == '\0'))) {
            System.out.println("in ORA Abs, X");
            byte hexVal1 = stringToHex(inputBuf.substring(i+5, i+7), 2);
            byte hexVal2 = stringToHex(inputBuf.substring(i+7, i+9), 2);

            mem[Controller.PCIndex++] = (byte) 0x1D;
            mem[Controller.PCIndex++] = hexVal2;  //little endian order, least significant byte
            mem[Controller.PCIndex++] = hexVal1;  //little endian order, most significant byte
            i+=11;
        }
        //ORA $FFFF, Y
        else if ((inputBuf.charAt(i+8) != ')' && inputBuf.charAt(i + 9) == ',' && inputBuf.charAt(i+11) == 'Y') && (inputBuf.charAt(i + 12) == '\n' || inputBuf.charAt(i + 12) == ' ' || inputBuf.charAt(i + 12) == '\0')) {
            System.out.println("in ORA Abs, Y");
            byte hexVal1 = stringToHex(inputBuf.substring(i+5, i+7), 2);
            byte hexVal2 = stringToHex(inputBuf.substring(i+7, i+9), 2);

            mem[Controller.PCIndex++] = (byte) 0x19;
            mem[Controller.PCIndex++] = hexVal2;  //little endian order, least significant byte
            mem[Controller.PCIndex++] = hexVal1;  //little endian order, most significant byte
            i+=11;
        }
        //ORA (ZP,X) ( AND ($FF, X) )
        else if((inputBuf.charAt(i+4) == '(' && inputBuf.charAt(i+11) == ')') && (inputBuf.charAt(i+12) == '\n' || inputBuf.charAt(i+12) == ' ' || inputBuf.charAt(i+12) == '\0') ){
            System.out.println("in ORA (ZP, X)");
            byte hexVal1 = stringToHex(inputBuf.substring(i+6,i+8), 2);

            mem[Controller.PCIndex++] = (byte) 0x01;
            mem[Controller.PCIndex++] = hexVal1;
            i+=11;
        }
        //ORA (ZP), Y   ( AND ($FF), Y )
        else if((inputBuf.charAt(i+4) == '(' && inputBuf.charAt(i+8) == ')') && (inputBuf.charAt(i+12) == '\n' || inputBuf.charAt(i+12) == ' ' || inputBuf.charAt(i+12) == '\0') ){
            System.out.println("in ORA (ZP), Y");
            byte hexVal1 = stringToHex(inputBuf.substring(i+6,i+8), 2);

            mem[Controller.PCIndex++] = (byte) 0x11;
            mem[Controller.PCIndex++] = hexVal1;
            i+=11;
        }
        else {
            System.out.println("syntax error in child ORA method: " + inputBuf.charAt(i) + ", " + i);
        }
        return i;
    }

    //PHA
    private static int instructionPHA(byte[] mem, String inputBuf, int i, Register A, Register X){
        System.out.println("in PHA");
        mem[Controller.PCIndex++] = (byte) 0x48;
        i+=3;
        return i;
    }
    //PHP
    private static int instructionPHP(byte[] mem, String inputBuf, int i, Register A, Register X){
        System.out.println("in PHP");
        mem[Controller.PCIndex++] = (byte) 0x08;
        i+=3;
        return i;
    }
    //PLA
    private static int instructionPLA(byte[] mem, String inputBuf, int i, Register A, Register X){
        System.out.println("in PLA");
        mem[Controller.PCIndex++] = (byte) 0x68;
        i+=3;
        return i;
    }
    //PLP
    private static int instructionPLP(byte[] mem, String inputBuf, int i, Register A, Register X){
        System.out.println("in PLP");
        mem[Controller.PCIndex++] = (byte) 0x28;
        i+=3;
        return i;
    }

    //rotate left by one
    private static int instructionROL(byte[] mem, String inputBuf, int i, Register X, Register Y){
        //ROL A
        if(inputBuf.charAt(i+4) == 'A'){
            System.out.println("in ROL A");
            mem[Controller.PCIndex++] = (byte) 0x2A;
            i+=4;
        }
        //ROL $FF
        else if(inputBuf.charAt(i + 4) == '$' && (inputBuf.charAt(i + 7) == '\n' || inputBuf.charAt(i + 7) == ' ') || inputBuf.charAt(i + 7) == '\0') {
            System.out.println("in ROL $FF");
            byte hexVal = stringToHex(inputBuf.substring(i+5, i+7), 2);
            mem[Controller.PCIndex++] = (byte) 0x26;
            mem[Controller.PCIndex++] = hexVal;
            i+=6;
        }
        //ROL $FF, X
        else if (inputBuf.charAt(i + 7) == ',' && (inputBuf.charAt(i + 10) == '\n' || inputBuf.charAt(i + 10) == ' ' || inputBuf.charAt(i + 10) == '\0')) {
            System.out.println("in ROL ZP, X");
            byte hexVal1 = stringToHex(inputBuf.substring(i+5, i+7), 2);
            mem[Controller.PCIndex++] = (byte) 0x36;
            mem[Controller.PCIndex++] = hexVal1;
            i+=9;
        }
        //ROL $FFFF
        else if (inputBuf.charAt(i + 4) == '$' && (inputBuf.charAt(i + 9) == '\n' || inputBuf.charAt(i + 9) == ' ' || inputBuf.charAt(i + 9) == '\0')) {
            System.out.println("in ROL $FFFF");
            byte hexVal1 = stringToHex(inputBuf.substring(i+5, i+7), 2);
            byte hexVal2 = stringToHex(inputBuf.substring(i+7, i+9), 2);
            mem[Controller.PCIndex++] = (byte) 0x2E;
            mem[Controller.PCIndex++] = hexVal2;  //little endian order, least significant byte
            mem[Controller.PCIndex++] = hexVal1;  //little endian order, most significant byte
            i+=8;   //changed from 7
        }
        //ROL $FFFF, X
        else if ( ((inputBuf.charAt(i+8) != ')' && inputBuf.charAt(i + 9) == ',' && inputBuf.charAt(i+11) == 'X') && (inputBuf.charAt(i + 12) == '\n' || inputBuf.charAt(i + 12) == ' ' || inputBuf.charAt(i + 12) == '\0'))) {
            System.out.println("in ROL Abs, X");
            byte hexVal1 = stringToHex(inputBuf.substring(i+5, i+7), 2);
            byte hexVal2 = stringToHex(inputBuf.substring(i+7, i+9), 2);

            mem[Controller.PCIndex++] = (byte) 0x3E;
            mem[Controller.PCIndex++] = hexVal2;  //little endian order, least significant byte
            mem[Controller.PCIndex++] = hexVal1;  //little endian order, most significant byte
            i+=11;
        }
        return i;
    }

    //rotate right by one
    private static int instructionROR(byte[] mem, String inputBuf, int i, Register X, Register Y){
        //ROR A
        if(inputBuf.charAt(i+4) == 'A'){
            System.out.println("in ROR A");
            mem[Controller.PCIndex++] = (byte) 0x6A;
            i+=4;
        }
        //ROR $FF
        else if(inputBuf.charAt(i + 4) == '$' && (inputBuf.charAt(i + 7) == '\n' || inputBuf.charAt(i + 7) == ' ') || inputBuf.charAt(i + 7) == '\0') {
            System.out.println("in ROR $FF");
            byte hexVal = stringToHex(inputBuf.substring(i+5, i+7), 2);
            mem[Controller.PCIndex++] = (byte) 0x66;
            mem[Controller.PCIndex++] = hexVal;
            i+=6;
        }
        //ROR $FF, X
        else if (inputBuf.charAt(i + 7) == ',' && (inputBuf.charAt(i + 10) == '\n' || inputBuf.charAt(i + 10) == ' ' || inputBuf.charAt(i + 10) == '\0')) {
            System.out.println("in ROR ZP, X");
            byte hexVal1 = stringToHex(inputBuf.substring(i+5, i+7), 2);
            mem[Controller.PCIndex++] = (byte) 0x76;
            mem[Controller.PCIndex++] = hexVal1;
            i+=9;
        }
        //ROR $FFFF
        else if (inputBuf.charAt(i + 4) == '$' && (inputBuf.charAt(i + 9) == '\n' || inputBuf.charAt(i + 9) == ' ' || inputBuf.charAt(i + 9) == '\0')) {
            System.out.println("in ROR $FFFF");
            byte hexVal1 = stringToHex(inputBuf.substring(i+5, i+7), 2);
            byte hexVal2 = stringToHex(inputBuf.substring(i+7, i+9), 2);
            mem[Controller.PCIndex++] = (byte) 0x6E;
            mem[Controller.PCIndex++] = hexVal2;  //little endian order, least significant byte
            mem[Controller.PCIndex++] = hexVal1;  //little endian order, most significant byte
            i+=8;   //changed from 7
        }
        //ROR $FFFF, X
        else if ( ((inputBuf.charAt(i+8) != ')' && inputBuf.charAt(i + 9) == ',' && inputBuf.charAt(i+11) == 'X') && (inputBuf.charAt(i + 12) == '\n' || inputBuf.charAt(i + 12) == ' ' || inputBuf.charAt(i + 12) == '\0'))) {
            System.out.println("in ROR Abs, X");
            byte hexVal1 = stringToHex(inputBuf.substring(i+5, i+7), 2);
            byte hexVal2 = stringToHex(inputBuf.substring(i+7, i+9), 2);

            mem[Controller.PCIndex++] = (byte) 0x7E;
            mem[Controller.PCIndex++] = hexVal2;  //little endian order, least significant byte
            mem[Controller.PCIndex++] = hexVal1;  //little endian order, most significant byte
            i+=11;
        }
        return i;
    }






    //a very hacky method to essentially convert chars to ints
    private static byte stringToHex(String str, int strLength){
        byte hexVal = 0x00;
        int j = 0;
        for(int i = strLength-1; i >= 0; i--, j++){
            switch(str.charAt(i)) {
                case '0':
                    hexVal += 0;
                    break;
                case '1':
                    hexVal += (byte) (1 * Math.pow(16, j));
                    break;
                case '2':
                    hexVal += (byte) (2 * Math.pow(16, j));
                    break;
                case '3':
                    hexVal += (byte) (3 * Math.pow(16, j));
                    break;
                case '4':
                    hexVal += (byte) (4 * Math.pow(16, j));
                    break;
                case '5':
                    hexVal += (byte) (5 * Math.pow(16, j));
                    break;
                case '6':
                    hexVal += (byte) (6 * Math.pow(16, j));
                    break;
                case '7':
                    hexVal += (byte) (7 * Math.pow(16, j));
                    break;
                case '8':
                    hexVal += (byte) (8 * Math.pow(16, j));
                    break;
                case '9':
                    hexVal += (byte) (9 * Math.pow(16, j));
                    break;
                case 'a':
                    hexVal += (byte) (10 * Math.pow(16, j));
                    break;
                case 'b':
                    hexVal += (byte) (11 * Math.pow(16, j));
                    break;
                case 'c':
                    hexVal += (byte) (12 * Math.pow(16, j));
                    break;
                case 'd':
                    hexVal += (byte) (13 * Math.pow(16, j));
                    break;
                case 'e':
                    hexVal += (byte) (14 * Math.pow(16, j));
                    break;
                case 'f':
                    hexVal += (byte) (15 * Math.pow(16, j));
                    break;
                //account for bloody upper case >:(
                case 'A':
                    hexVal += (byte) (10 * Math.pow(16, j));
                    break;
                case 'B':
                    hexVal += (byte) (11 * Math.pow(16, j));
                    break;
                case 'C':
                    hexVal += (byte) (12 * Math.pow(16, j));
                    break;
                case 'D':
                    hexVal += (byte) (13 * Math.pow(16, j));
                    break;
                case 'E':
                    hexVal += (byte) (14 * Math.pow(16, j));
                    break;
                case 'F':
                    hexVal += (byte) (15 * Math.pow(16, j));
                    break;
            }
        }
        return hexVal;
    }


    //scan through the list of labels for the label of name "str", return the corresponding address
    private static int findLabelAddress(String str, List<Label> labelList){
        int addr=0;

        System.out.println("string: " + str);

        for(int i = 0; i < labelList.size(); i++){
            if(labelList.get(i).name.equals(str)){
                addr = labelList.get(i).address;
                break;  //found the correct label, exit loop
            }
        }
        return addr;
    }

    //get the length of a label name
    private static int getLabelLength(String str, int index){
        int len=0;

        for(len = index+4; ( (str.charAt(len) != ' ') && (str.charAt(len) != '\n') && (str.charAt(len) != ')') ); len++) {
            System.out.println(str.charAt(len));
        }
        return (len-index-4);
    }
}

class Execute{
    public static void execute(byte[] mem, boolean step, Register A, Register X, Register Y
            , FLAGRegister registerFlag, bigRegister stackPointer, bigRegister programCounter, TextArea hexdumpDisplay, TextArea registerDisplay, TextArea terminal){

        int i = Controller.PC, j=0, k=0, oldVal=0, newVal=0, tmp_i=0; byte tmp_b=0;

        //TODO will this cause a bug if you try and branch to address zero, since the loop will break out of
        //TODO solution: I don't think so, as the value seen by the while loop is always an opcode!

        //the multiplication of the most significant byte in the absolute addressing mode by 2^8, corresponds to a left shift of 8 bits.
        while(mem[i] != 0){ //while ! BRK_
            switch(mem[i]){

                //*********************
                /* LDA Instructions */
                //*********************

                //LDA #Imm
                case (byte) 0xA9:
                    A.val = mem[i+1];
                    i+=2;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;
                //LDA $FF
                case (byte) 0xA5:
                    A.val = mem[mem[i+1]];
                    i+=2;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;
                //LDA $FF, X
                case (byte) 0xB5:
                    A.val = mem[mem[i+1] + X.val];
                    i+=2;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;
                //LDA $FFFF
                case (byte) 0xAD:
                    j = (int) (mem[i+2] * Math.pow(2,8));
                    A.val = mem[mem[i+1] + j];
                    i+=3;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;
                //LDA $FFFF, X
                case (byte) 0xBD:
                    j = (int) (mem[i+2] * Math.pow(2,8));
                    A.val = mem[mem[i+1] + j + X.val];
                    i+=3;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;
                //LDA $FFFF, Y
                case (byte) 0xB9:
                    j = (int) (mem[i+2] * Math.pow(2,8));
                    A.val = mem[mem[i+1] + j + Y.val];
                    i+=3;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;
                //LDA (ZP, X)
                case (byte) 0xA1:
                    k = mem[i+1] + X.val;
                    j = (int) (mem[k+1] * Math.pow(2,8));
                    A.val = mem[j + mem[k]];

                    setFlagRegister(registerFlag, -1, A.val);
                    i+=2;
                    break;
                //LDA (ZP), Y
                case (byte) 0xB1:
                    j = mem[i+1];
                    k = (int) (mem[j+1] * Math.pow(2,8));
                    A.val = mem[k + mem[j] + Y.val];
                    setFlagRegister(registerFlag, -1, A.val);
                    i+=2;
                    break;

                //*********************
                /* LDX Instructions */
                //*********************

                //LDX #Imm
                case (byte) 0xA2:
                    X.val = mem[i+1];
                    i+=2;
                    setFlagRegister(registerFlag, -1, X.val);
                    break;
                //LDX $FF
                case (byte) 0xA6:
                    X.val = mem[mem[i+1]];
                    i+=2;
                    setFlagRegister(registerFlag, -1, X.val);
                    break;
                //LDX $FF, Y
                case (byte) 0xB6:
                    X.val = mem[mem[i+1] + Y.val];
                    i+=2;
                    setFlagRegister(registerFlag, -1, X.val);
                    break;
                //LDX $FFFF
                case (byte) 0xAE:
                    j = (int) (mem[i+2] * Math.pow(2,8));
                    X.val = mem[mem[i+1] + j];
                    i+=3;
                    setFlagRegister(registerFlag, -1, X.val);
                    break;
                //LDX $FFFF, Y
                case (byte) 0xBE:
                    j = (int) (mem[i+2] * Math.pow(2,8));
                    X.val = mem[mem[i+1] + j + Y.val];
                    i+=3;
                    setFlagRegister(registerFlag, -1, X.val);
                    break;

                //*********************
                /* LDY Instructions */
                //*********************

                //LDY #Imm
                case (byte) 0xA0:
                    Y.val = mem[i+1];
                    i+=2;
                    setFlagRegister(registerFlag, -1, Y.val);
                    break;
                //LDY $FF
                case (byte) 0xA4:
                    Y.val = mem[mem[i+1]];
                    i+=2;
                    setFlagRegister(registerFlag, -1, Y.val);
                    break;
                //LDY $FF, X
                case (byte) 0xB4:
                    Y.val = mem[mem[i+1] + Y.val];
                    i+=2;
                    setFlagRegister(registerFlag, -1, Y.val);
                    break;
                //LDY $FFFF
                case (byte) 0xAC:
                    j = (int) (mem[i+2] * Math.pow(2,8));
                    Y.val = mem[mem[i+1] + j];
                    i+=3;
                    setFlagRegister(registerFlag, -1, Y.val);
                    break;
                //LDY $FFFF, X
                case (byte) 0xBC:
                    j = (int) (mem[i+2] * Math.pow(2,8));
                    Y.val = mem[mem[i+1] + j + X.val];
                    i+=3;
                    setFlagRegister(registerFlag, -1, Y.val);
                    break;

                //**************************
                /* Transfer Instructions */
                //**************************

                //TAX
                case (byte) 0xAA:
                    X.val = A.val;
                    i++;
                    setFlagRegister(registerFlag, -1, X.val);
                    break;
                //TAY
                case (byte) 0xA8:
                    Y.val = A.val;
                    i++;
                    setFlagRegister(registerFlag, -1, Y.val);
                    break;
                //TSX
                case (byte) 0xBA:
                    X.val = (byte) stackPointer.val;  //TODO: is this right? this will work if and only if the cast to a byte truncates to only the least significant 8 bits
                    i++;
                    setFlagRegister(registerFlag, -1, X.val);
                    break;
                //TXA
                case (byte) 0x8A:
                    A.val = X.val;
                    i++;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;
                //TXS
                case (byte) 0x9A:
                    stackPointer.val = X.val;
                    i++;
                    setFlagRegister(registerFlag, -1, stackPointer.val);
                    break;
                //TYA
                case (byte) 0x98:
                    A.val = Y.val;
                    i++;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;

                //*********************
                /* STA Instructions */
                //*********************

                //STA $FF
                case (byte) 0x85:
                    mem[mem[i+1]] = A.val;
                    i+=2;
                    break;
                //STA $FF, X
                case (byte) 0x95:
                    mem[mem[i+1] + X.val] = A.val;
                    i+=2;
                    break;
                //STA $FFFF
                case (byte) 0x8D:
                    j = (int) (mem[i+2] * Math.pow(2,8));
                    mem[mem[i+1] + j] = A.val;
                    i+=3;
                    break;
                //STA Abs, X
                case (byte) 0x9D:
                    j = (int) (mem[i+2] * Math.pow(2,8));
                    mem[mem[i+1] + j + X.val] = A.val;
                    i+=3;
                    break;
                //STA Abs, Y
                case (byte) 0x99:
                    j = (int) (mem[i+2] * Math.pow(2,8));
                    mem[mem[i+1] + j + Y.val] = A.val;
                    i+=3;
                    break;
                //STA (ZP, X)
                case (byte) 0x81:
                    k = mem[i+1] + X.val;
                    j = (int) (mem[k+1] * Math.pow(2,8));
                    mem[j + mem[k]] = A.val;
                    i+=2;
                    break;
                //STA (ZP), Y
                case (byte) 0x91:
                    j = mem[i+1];
                    k = (int) (mem[j+1] * Math.pow(2,8));
                    mem[k + mem[j] + Y.val] = A.val;
                    i+=2;
                    break;

                //*********************
                /* STX Instructions */
                //*********************

                //STX ZP
                case (byte) 0x86:
                    mem[mem[i+1]] = X.val;
                    i+=2;
                    break;
                //STX ZP, Y
                case (byte) 0x96:
                    mem[mem[i+1] + Y.val] = X.val;
                    i+=2;
                    break;
                //STX Abs
                case (byte) 0x8E:
                    j = (int) (mem[i+2] * Math.pow(2,8));
                    mem[mem[i+1] + j] = X.val;
                    i+=3;
                    break;

                //*********************
                /* STY Instructions */
                //*********************

                //STY ZP
                case (byte) 0x84:
                    mem[mem[i+1]] = Y.val;
                    i+=2;
                    break;
                //STY ZP, X
                case (byte) 0x94:
                    mem[mem[i+1] + X.val] = Y.val;
                    i+=2;
                    break;
                //STY Abs
                case (byte) 0x8C:
                    j = (int) (mem[i+2] * Math.pow(2,8));
                    mem[mem[i+1] + j] = Y.val;
                    i+=3;
                    break;

                //*********************
                /* ADC Instructions */
                //*********************

                //ADC #Imm
                case (byte) 0x69:
                    A.val += mem[i+1] + registerFlag.C;
                    i+=2;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;
                //ADC ZP
                case (byte) 0x65:
                    A.val += mem[mem[i+1]] + registerFlag.C;
                    i+=2;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;
                //ADC ZP, X
                case (byte) 0x75:
                    A.val += mem[mem[i+1] + X.val] + registerFlag.C;
                    i+=2;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;
                //ADC Abs
                case (byte) 0x6D:
                    j = (int) (mem[i+2] * Math.pow(2,8));
                    A.val += mem[j + mem[i+1]] + registerFlag.C;
                    i+=3;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;
                //ADC Abs, X
                case (byte) 0x7D:
                    j = (int) (mem[i+2] * Math.pow(2,8));
                    A.val += mem[j + mem[i+1] + X.val] + registerFlag.C;
                    i+=3;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;
                //ADC Abs, Y
                case (byte) 0x79:
                    j = (int) (mem[i+2] * Math.pow(2,8));
                    A.val += mem[j + mem[i+1] + Y.val] + registerFlag.C;
                    i+=3;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;
                //ADC (ZP, X)
                case (byte) 0x61:
                    k = mem[i+1] + X.val;
                    j = (int) (mem[k+1] * Math.pow(2,8));
                    A.val += mem[j + mem[k]] + registerFlag.C;
                    setFlagRegister(registerFlag, -1, A.val);
                    i+=2;
                    break;
                //ADC (ZP), Y
                case (byte) 0x71:
                    j = mem[i+1];
                    k = (int) (mem[j+1] * Math.pow(2,8));
                    A.val += mem[k + mem[j] + Y.val] + registerFlag.C;
                    setFlagRegister(registerFlag, -1, A.val);
                    i+=2;
                    break;

                //*********************
                /* AND Instructions */
                //*********************

                //AND #Imm
                case (byte) 0x29:
                    A.val &= mem[i+1];
                    i+=2;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;
                //AND ZP
                case (byte) 0x25:
                    A.val &= mem[mem[i+1]];
                    i+=2;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;
                //AND ZP, X
                case (byte) 0x35:
                    A.val &= mem[mem[i+1] + X.val];
                    i+=2;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;
                //AND Abs
                case (byte) 0x2D:
                    j = (int) (mem[i+2] * Math.pow(2,8));
                    A.val &= mem[j + mem[i+1]];
                    i+=3;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;
                //AND Abs, X
                case (byte) 0x3D:
                    j = (int) (mem[i+2] * Math.pow(2,8));
                    A.val &= mem[j + mem[i+1] + X.val];
                    i+=3;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;
                //AND Abs, Y
                case (byte) 0x39:
                    j = (int) (mem[i+2] * Math.pow(2,8));
                    A.val &= mem[j + mem[i+1] + Y.val];
                    i+=3;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;
                //AND (ZP, X)
                case (byte) 0x21:
                    k = mem[i+1] + X.val;
                    j = (int) (mem[k+1] * Math.pow(2,8));
                    A.val &= mem[j + mem[k]];
                    setFlagRegister(registerFlag, -1, A.val);
                    i+=2;
                    break;
                //AND (ZP), Y
                case (byte) 0x31:
                    j = mem[i+1];
                    k = (int) (mem[j+1] * Math.pow(2,8));
                    A.val &= mem[k + mem[j] + Y.val];
                    setFlagRegister(registerFlag, -1, A.val);
                    i+=2;
                    break;

                //*********************
                /* ASL Instructions */
                //*********************

                //ASL A
                case (byte) 0x0A:
                    A.val <<= 1;
                    A.val &= 0xFF;
                    i++;
                    break;
                //ASL ZP
                case (byte) 0x06:
                    mem[mem[i+1]] <<= 1;
                    mem[mem[i+1]] &= 0xFF;  //truncate to 8 bits
                    i+=2;
                    break;
                //ASL ZP, X
                case (byte) 0x16:
                    mem[mem[i+1] + X.val] <<= 1;
                    mem[mem[i+1] + X.val] &= 0xFF;
                    i+=2;
                    break;
                //ASL Abs
                case (byte) 0x0E:
                    j = (int) (mem[i+2] * Math.pow(2,8));
                    mem[mem[i+1] + j] <<= 1;
                    mem[mem[i+1]+ j] &= 0xFF;
                    i+=3;
                    break;
                //ASL Abs, X
                case (byte) 0x1E:
                    j = (int) (mem[i+2] * Math.pow(2,8));
                    mem[mem[i+1] + j + X.val] <<= 1;
                    mem[mem[i+1]+ j + X.val] &= 0xFF;
                    i+=3;
                    break;

                //***********************
                /* Branch Instructions */
                //***********************
                //reassign 'i' to the value given by the Rel address/label

                //BCC Rel
                case (byte) 0x90:
                    if(registerFlag.C == 0)
                        i = mem[i+1];
                    break;
                //BCS Rel
                case (byte) 0xB0:
                    if(registerFlag.C == 1)
                        i = mem[i+1];
                    break;
                //BEQ Rel
                case (byte) 0xF0:
                    if(registerFlag.Z == 1)
                        i = mem[i+1];
                    break;
                //BIT ZP
                case (byte) 0x24:
                    tmp_i = (A.val & mem[mem[i+1]]);
                    if(tmp_i == 0)
                        registerFlag.Z = 1;
                    else
                        registerFlag.Z = 0;

                    registerFlag.N = (byte)((mem[mem[i+1]] >> 7) & 0b01);
                    registerFlag.V = (byte)((mem[mem[i+1]] >> 6) & 0b01);
                    break;
                //BIT Abs
                case (byte) 0x2C:
                    j = (int) (mem[i+2] * Math.pow(2,8));
                    tmp_i = (A.val & mem[mem[i+1] + j]);
                    if(tmp_i == 0)
                        registerFlag.Z = 1;
                    else
                        registerFlag.Z = 0;

                    registerFlag.N = (byte)((mem[mem[i+1] + j] >> 7) & 0b01);
                    registerFlag.V = (byte)((mem[mem[i+1] + j] >> 6) & 0b01);
                    break;
                //BMI Rel
                case (byte) 0x30:
                    if(registerFlag.N == 1)
                        i = mem[i+1];
                    break;
                //BNE Rel
                case (byte) 0xD0:
                    if(registerFlag.Z == 0)
                        i = mem[i+1];
                    break;
                //BPL Rel
                case (byte) 0x10:
                    if(registerFlag.N == 0)
                        i = mem[i+1];
                    break;
                //BRK
                case (byte) 0x00:
                    //this case is never executed since if mem[i] == 0
                    //the while loop will exit automatically anyway.

                    break;
                //BVC Rel
                case (byte) 0x50:
                    if(registerFlag.V == 0)
                        i = mem[i+1];
                    break;
                //BVS Rel
                case (byte) 0x70:
                    if(registerFlag.V == 1)
                        i = mem[i+1];
                    break;

                //****************************
                /* Clear flags instructions */
                //****************************

                //CLC
                case (byte) 0x18:
                    registerFlag.C = 0;
                    i+=1;
                    break;
                //CLD
                case (byte) 0xD8:
                    registerFlag.D = 0;
                    i+=1;
                    break;
                //CLI
                case (byte) 0x58:
                    registerFlag.I = 0;
                    i+=1;
                    break;
                //CLV
                case (byte) 0xB8:
                    registerFlag.V = 0;
                    i+=1;
                    break;

                //**************************
                /* Set flags instructions */
                //**************************

                //SEC
                case (byte) 0x38:
                    registerFlag.C = 1;
                    i+=1;
                    break;
                //SED
                case (byte) 0xF8:
                    registerFlag.D = 1;
                    i+=1;
                    break;
                //SEI
                case (byte) 0x78:
                    registerFlag.I = 1;
                    i+=1;
                    break;

                //*********************
                /* CMP Instructions */
                //*********************

                //CMP #Imm
                case (byte) 0xC9:
                    tmp_b = (byte) (A.val - mem[i+1]);
                    registerFlag.Z = (byte) ((tmp_b == 0) ? 1 : 0);
                    registerFlag.C = (byte) ((tmp_b < 0) ? 1 : 0);
                    registerFlag.N = (byte) (tmp_b >> 7);   //MSB of result

                    i+=2;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;
                //CMP ZP
                case (byte) 0xC5:
                    tmp_b = (byte) (A.val - mem[mem[i+1]]);
                    registerFlag.Z = (byte) ((tmp_b == 0) ? 1 : 0);
                    registerFlag.C = (byte) ((tmp_b < 0) ? 1 : 0);
                    registerFlag.N = (byte) (tmp_b >> 7);   //MSB of result

                    i+=2;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;
                //CMP ZP, X
                case (byte) 0xD5:
                    tmp_b = (byte) (A.val - mem[mem[i+1] + X.val]);
                    registerFlag.Z = (byte) ((tmp_b == 0) ? 1 : 0);
                    registerFlag.C = (byte) ((tmp_b < 0) ? 1 : 0);
                    registerFlag.N = (byte) (tmp_b >> 7);   //MSB of result

                    i+=2;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;
                //CMP Abs
                case (byte) 0xCD:
                    j = (int) (mem[i+2] * Math.pow(2,8));
                    tmp_b = (byte) (A.val - mem[j + mem[i+1]]);
                    registerFlag.Z = (byte) ((tmp_b == 0) ? 1 : 0);
                    registerFlag.C = (byte) ((tmp_b < 0) ? 1 : 0);
                    registerFlag.N = (byte) (tmp_b >> 7);   //MSB of result

                    i+=3;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;
                //CMP Abs, X
                case (byte) 0xDD:
                    j = (int) (mem[i+2] * Math.pow(2,8));
                    tmp_b = (byte) (A.val - mem[j + mem[i+1] + X.val]);
                    registerFlag.Z = (byte) ((tmp_b == 0) ? 1 : 0);
                    registerFlag.C = (byte) ((tmp_b < 0) ? 1 : 0);
                    registerFlag.N = (byte) (tmp_b >> 7);   //MSB of result

                    i+=3;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;
                //CMP Abs, Y
                case (byte) 0xD9:
                    j = (int) (mem[i+2] * Math.pow(2,8));
                    tmp_b = (byte) (A.val - mem[j + mem[i+1] + Y.val]);
                    registerFlag.Z = (byte) ((tmp_b == 0) ? 1 : 0);
                    registerFlag.C = (byte) ((tmp_b < 0) ? 1 : 0);
                    registerFlag.N = (byte) (tmp_b >> 7);   //MSB of result

                    i+=3;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;
                //CMP (ZP, X)
                case (byte) 0xC1:
                    k = mem[i+1] + X.val;
                    j = (int) (mem[k+1] * Math.pow(2,8));
                    tmp_b = (byte) (A.val - mem[j + mem[k]]);
                    registerFlag.Z = (byte) ((tmp_b == 0) ? 1 : 0);
                    registerFlag.C = (byte) ((tmp_b < 0) ? 1 : 0);
                    registerFlag.N = (byte) (tmp_b >> 7);   //MSB of result

                    i+=2;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;
                //CMP (ZP), Y
                case (byte) 0xD1:
                    j = mem[i+1];
                    k = (int) (mem[j+1] * Math.pow(2,8));
                    tmp_b = (byte) (A.val - mem[k + mem[j] + Y.val]);
                    registerFlag.Z = (byte) ((tmp_b == 0) ? 1 : 0);
                    registerFlag.C = (byte) ((tmp_b < 0) ? 1 : 0);
                    registerFlag.N = (byte) (tmp_b >> 7);   //MSB of result

                    i+=2;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;

                //*********************
                /* CPX Instructions */
                //*********************

                //CPX #Imm
                case (byte) 0xE0:
                    tmp_b = (byte) (X.val - mem[i+1]);
                    registerFlag.Z = (byte) ((tmp_b == 0) ? 1 : 0);
                    registerFlag.C = (byte) ((tmp_b < 0) ? 1 : 0);
                    registerFlag.N = (byte) (tmp_b >> 7);   //MSB of result

                    i+=2;
                    setFlagRegister(registerFlag, -1, X.val);
                    break;
                //CPX ZP
                case (byte) 0xE4:
                    tmp_b = (byte) (X.val - mem[mem[i+1]]);
                    registerFlag.Z = (byte) ((tmp_b == 0) ? 1 : 0);
                    registerFlag.C = (byte) ((tmp_b < 0) ? 1 : 0);
                    registerFlag.N = (byte) (tmp_b >> 7);   //MSB of result

                    i+=2;
                    setFlagRegister(registerFlag, -1, X.val);
                    break;
                //CPX Abs
                case (byte) 0xEC:
                    j = (int) (mem[i+2] * Math.pow(2,8));
                    tmp_b = (byte) (X.val - mem[j + mem[i+1]]);
                    registerFlag.Z = (byte) ((tmp_b == 0) ? 1 : 0);
                    registerFlag.C = (byte) ((tmp_b < 0) ? 1 : 0);
                    registerFlag.N = (byte) (tmp_b >> 7);   //MSB of result

                    i+=3;
                    setFlagRegister(registerFlag, -1, X.val);
                    break;


                //*********************
                /* CPY Instructions */
                //*********************

                //CPY #Imm
                case (byte) 0xC0:
                    tmp_b = (byte) (Y.val - mem[i+1]);
                    registerFlag.Z = (byte) ((tmp_b == 0) ? 1 : 0);
                    registerFlag.C = (byte) ((tmp_b < 0) ? 1 : 0);
                    registerFlag.N = (byte) (tmp_b >> 7);   //MSB of result

                    i+=2;
                    setFlagRegister(registerFlag, -1, Y.val);
                    break;
                //CPY ZP
                case (byte) 0xC4:
                    tmp_b = (byte) (Y.val - mem[mem[i+1]]);
                    registerFlag.Z = (byte) ((tmp_b == 0) ? 1 : 0);
                    registerFlag.C = (byte) ((tmp_b < 0) ? 1 : 0);
                    registerFlag.N = (byte) (tmp_b >> 7);   //MSB of result

                    i+=2;
                    setFlagRegister(registerFlag, -1, Y.val);
                    break;
                //CPY Abs
                case (byte) 0xCC:
                    j = (int) (mem[i+2] * Math.pow(2,8));
                    tmp_b = (byte) (Y.val - mem[j + mem[i+1]]);
                    registerFlag.Z = (byte) ((tmp_b == 0) ? 1 : 0);
                    registerFlag.C = (byte) ((tmp_b < 0) ? 1 : 0);
                    registerFlag.N = (byte) (tmp_b >> 7);   //MSB of result

                    i+=3;
                    setFlagRegister(registerFlag, -1, Y.val);
                    break;


                //*********************
                /* DEC Instructions */
                //*********************

                //DEC ZP
                case (byte) 0xC6:
                    mem[mem[i+1]]--;

                    i+=2;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;
                //DEC ZP, X
                case (byte) 0xD6:
                    mem[mem[i+1] + X.val]--;

                    i+=2;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;
                //DEC Abs
                case (byte) 0xCE:
                    j = (int) (mem[i+2] * Math.pow(2,8));
                    mem[j + mem[i+1]]--;

                    i+=3;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;
                //DEC Abs, X
                case (byte) 0xDE:
                    j = (int) (mem[i+2] * Math.pow(2,8));
                    mem[j + mem[i+1] + X.val]--;

                    i+=3;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;


                //********************
                /* DEX Instruction */
                //********************

                //DEX
                case (byte) 0xCA:
                    X.val--;

                    i+=1;
                    setFlagRegister(registerFlag, -1, Y.val);
                    break;

                //********************
                /* DEY Instruction */
                //********************

                //DEY
                case (byte) 0x88:
                    Y.val--;

                    i+=1;
                    setFlagRegister(registerFlag, -1, Y.val);
                    break;

                //********************
                /* EOR Instruction */
                //********************

                //EOR #Imm
                case (byte) 0x49:
                    A.val ^= mem[i+1];
                    i+=2;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;
                //EOR ZP
                case (byte) 0x45:
                    A.val ^= mem[mem[i+1]];
                    i+=2;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;
                //EOR ZP, X
                case (byte) 0x55:
                    A.val ^= mem[mem[i+1] + X.val];
                    i+=2;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;
                //EOR Abs
                case (byte) 0x4D:
                    j = (int) (mem[i+2] * Math.pow(2,8));
                    A.val ^= mem[j + mem[i+1]];
                    i+=3;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;
                //EOR Abs, X
                case (byte) 0x5D:
                    j = (int) (mem[i+2] * Math.pow(2,8));
                    A.val ^= mem[j + mem[i+1] + X.val];
                    i+=3;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;
                //EOR Abs, Y
                case (byte) 0x59:
                    j = (int) (mem[i+2] * Math.pow(2,8));
                    A.val ^= mem[j + mem[i+1] + Y.val];
                    i+=3;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;
                //EOR (ZP, X)
                case (byte) 0x41:
                    k = mem[i+1] + X.val;
                    j = (int) (mem[k+1] * Math.pow(2,8));
                    A.val ^= mem[j + mem[k]];
                    setFlagRegister(registerFlag, -1, A.val);
                    i+=2;
                    break;
                //EOR (ZP), Y
                case (byte) 0x51:
                    j = mem[i+1];
                    k = (int) (mem[j+1] * Math.pow(2,8));
                    A.val ^= mem[k + mem[j] + Y.val];
                    setFlagRegister(registerFlag, -1, A.val);
                    i+=2;
                    break;


                //*********************
                /* INC Instructions */
                //*********************

                //INC ZP
                case (byte) 0xE6:
                    mem[mem[i+1]]++;

                    i+=2;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;
                //INC ZP, X
                case (byte) 0xF6:
                    mem[mem[i+1] + X.val]++;

                    i+=2;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;
                //INC Abs
                case (byte) 0xEE:
                    j = (int) (mem[i+2] * Math.pow(2,8));
                    mem[j + mem[i+1]]++;

                    i+=3;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;
                //INC Abs, X
                case (byte) 0xFE:
                    j = (int) (mem[i+2] * Math.pow(2,8));
                    mem[j + mem[i+1] + X.val]++;

                    i+=3;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;


                //********************
                /* INX Instruction */
                //********************

                //INX
                case (byte) 0xE8:
                    X.val++;

                    i+=1;
                    setFlagRegister(registerFlag, -1, Y.val);
                    break;

                //********************
                /* INY Instruction */
                //********************

                //INY
                case (byte) 0xC8:
                    Y.val++;

                    i+=1;
                    setFlagRegister(registerFlag, -1, Y.val);
                    break;

                //********************
                /* JMP Instructions */
                //********************

                //JMP label
                case (byte) 0x4C:
                    j = (int) (mem[i+2] * Math.pow(2,8));
                    i = j + mem[i+1];
                    break;
                //JMP (label)
                case (byte) 0x6C:
                    j = (int) (mem[i+2] * Math.pow(2,8));
                    i = mem[j + mem[i+1]];
                    break;

                //********************
                /* JSR Instructions */
                //********************

                //JSR label
                case (byte) 0x20:
                    j = (int) (mem[i+2] * Math.pow(2,8));
                    mem[stackPointer.val++] = i;   //push the current address to the stack, and increment to next address TODO: should this take up two bytes as a 16 bit address?
                    i = j + mem[i+1];
                    break;

                //*********************
                /* LSR Instructions */
                //*********************

                //LSR A
                case (byte) 0x4A:
                    A.val >>= 1;
                    A.val &= 0xFF;
                    i++;
                    break;
                //LSR ZP
                case (byte) 0x46:
                    mem[mem[i+1]] >>= 1;
                    mem[mem[i+1]] &= 0xFF;  //truncate to 8 bits
                    i+=2;
                    break;
                //LSR ZP, X
                case (byte) 0x56:
                    mem[mem[i+1] + X.val] >>= 1;
                    mem[mem[i+1] + X.val] &= 0xFF;
                    i+=2;
                    break;
                //LSR Abs
                case (byte) 0x4E:
                    j = (int) (mem[i+2] * Math.pow(2,8));
                    mem[mem[i+1] + j] >>= 1;
                    mem[mem[i+1]+ j] &= 0xFF;
                    i+=3;
                    break;
                //LSR Abs, X
                case (byte) 0x5E:
                    j = (int) (mem[i+2] * Math.pow(2,8));
                    mem[mem[i+1] + j + X.val] >>= 1;
                    mem[mem[i+1]+ j + X.val] &= 0xFF;
                    i+=3;
                    break;

                //********************
                /* NOP Instruction */
                //********************

                //NOP
                case (byte) 0xEA:
                    //NOP, do nothing
                    i+=1;
                    break;

                //*********************
                /* ORA Instructions */
                //*********************

                //ORA #Imm
                case (byte) 0x09:
                    A.val |= mem[i+1];
                    i+=2;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;
                //ORA ZP
                case (byte) 0x05:
                    A.val |= mem[mem[i+1]];
                    i+=2;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;
                //ORA ZP, X
                case (byte) 0x15:
                    A.val |= mem[mem[i+1] + X.val];
                    i+=2;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;
                //ORA Abs
                case (byte) 0x0D:
                    j = (int) (mem[i+2] * Math.pow(2,8));
                    A.val |= mem[j + mem[i+1]];
                    i+=3;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;
                //ORA Abs, X
                case (byte) 0x1D:
                    j = (int) (mem[i+2] * Math.pow(2,8));
                    A.val |= mem[j + mem[i+1] + X.val];
                    i+=3;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;
                //ORA Abs, Y
                case (byte) 0x19:
                    j = (int) (mem[i+2] * Math.pow(2,8));
                    A.val |= mem[j + mem[i+1] + Y.val];
                    i+=3;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;
                //ORA (ZP, X)
                case (byte) 0x01:
                    k = mem[i+1] + X.val;
                    j = (int) (mem[k+1] * Math.pow(2,8));
                    A.val |= mem[j + mem[k]];
                    setFlagRegister(registerFlag, -1, A.val);
                    i+=2;
                    break;
                //ORA (ZP), Y
                case (byte) 0x11:
                    j = mem[i+1];
                    k = (int) (mem[j+1] * Math.pow(2,8));
                    A.val |= mem[k + mem[j] + Y.val];
                    setFlagRegister(registerFlag, -1, A.val);
                    i+=2;
                    break;

                //********************************
                /* PHA/PHP/PLA/PLP Instructions */
                //********************************

                //PHA
                case (byte) 0x48:
                    mem[stackPointer.val++] = A.val;

                    i+=1;
                    break;
                //PHP
                case (byte) 0x08:
                    tmp_b |= registerFlag.N; tmp_b <<= 1;
                    tmp_b |= registerFlag.Z; tmp_b <<= 1;
                    tmp_b |= registerFlag.C; tmp_b <<= 1;
                    tmp_b |= registerFlag.I; tmp_b <<= 1;
                    tmp_b |= registerFlag.D; tmp_b <<= 1;
                    tmp_b |= registerFlag.V; tmp_b <<= 1;

                    mem[stackPointer.val++] = tmp_b;

                    i+=1;
                    break;
                //PLA
                case (byte) 0x68:
                    A.val = mem[stackPointer.val--];

                    i+=1;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;
                //PLP
                case (byte) 0x28:
                    registerFlag.V |= tmp_b; registerFlag.V &= 0x01; tmp_b >>= 1;
                    registerFlag.D |= tmp_b; registerFlag.D &= 0x01; tmp_b >>= 1;
                    registerFlag.I |= tmp_b; registerFlag.I &= 0x01; tmp_b >>= 1;
                    registerFlag.C |= tmp_b; registerFlag.C &= 0x01; tmp_b >>= 1;
                    registerFlag.Z |= tmp_b; registerFlag.Z &= 0x01; tmp_b >>= 1;
                    registerFlag.N |= tmp_b; registerFlag.N &= 0x01; tmp_b >>= 1;

                    i+=1;
                    setFlagRegister(registerFlag, -1, A.val);
                    break;

                //*********************
                /* ROL Instructions */  //TODO: UP TO HERE! Unfinished!!!
                //*********************

                //ROL A
                case (byte) 0x2A:
                    A.val <<= 1;
                    tmp_b = A.val;



                    A.val &= 0xFF;
                    i++;
                    break;
                //ROL ZP
                case (byte) 0x26:
                    mem[mem[i+1]] <<= 1;
                    mem[mem[i+1]] &= 0xFF;  //truncate to 8 bits
                    i+=2;
                    break;
                //ROL ZP, X
                case (byte) 0x36:
                    mem[mem[i+1] + X.val] <<= 1;
                    mem[mem[i+1] + X.val] &= 0xFF;
                    i+=2;
                    break;
                //ROL Abs
                case (byte) 0x2E:
                    j = (int) (mem[i+2] * Math.pow(2,8));
                    mem[mem[i+1] + j] <<= 1;
                    mem[mem[i+1]+ j] &= 0xFF;
                    i+=3;
                    break;
                //ROL Abs, X
                case (byte) 0x3E:
                    j = (int) (mem[i+2] * Math.pow(2,8));
                    mem[mem[i+1] + j + X.val] <<= 1;
                    mem[mem[i+1]+ j + X.val] &= 0xFF;
                    i+=3;
                    break;






                default:
                    System.out.println("!!! unrecognised opcode !!!");
            }


            //will changes to the flagRegister in setFlagRegister() change the value in this function???
            //setFlagRegister(registerFlag, oldVal, newVal);

            Controller.PC = i;
            programCounter.val =  Controller.PC;
            Controller.updateDisplays(mem, hexdumpDisplay, registerDisplay, A, X, Y, registerFlag, programCounter);

            if(mem[i] == 0){
                terminal.setText("Program Finished");
            }

            if(step){
                return;
            }
        }
    }

    //TODO: THE PLAN for flags implementation -> every single instruction that may change a flag bit must have
    //      the value before modification stored in 'oldVal', and the value after modification stored in 'newVal'.
    //      Then you can simply figure out the appropriate flags to set given these two values, and the operation that occurred.
    private static void setFlagRegister(FLAGRegister registerFlag, int oldVal, int newVal){
        //the old and the new value to be compared are passed into this function, determine which flags should be set
        //and set the appropriate flags in the registerFlag register.

        //'N' flag WRONG -> instead do a check for the MSB, if 1 then N is set, if 0 then N clear
        //so just simply set the value of registerFlag.n = MSB...
        /*if(newVal < 0) registerFlag.N = 1;
        else registerFlag.N = 0;*/

        //'Z' flag
        /*if(newVal == 0) registerFlag.Z = 1;
        else registerFlag.Z = 0;*/

        //if oldVal = -1, then the below C and V flags do not need to be determined
        //must have more constants for when onle a single one of C or V needs to be determined
        /*if(!(oldVal == -1)) {
            //'C' flag
            //might have to check for each case: add, shift

            //'V' flag
            //XOR the MSB of oldVal and newVal (?)
        }*/
    }
}

class Label{
    String name;
    int address;
}

class Register {
    byte val;   //changed to byte from int
    Register() {
        val = 0x00;
    }
}

class bigRegister{
    int val;
    bigRegister() {
        val = 0x00;
    }
}

class FLAGRegister{
    byte N,Z,C,I,D,V;  //changed from public byte N,Z,C,I,D,V;

    public void setFLAGS(byte N, byte Z, byte C, byte I, byte D, byte V) {
        this.N = N;
        this.Z = Z;
        this.C = C;
        this.I = I;
        this.D = D;
        this.V = V;
    }
}
