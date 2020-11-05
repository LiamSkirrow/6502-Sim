package sample;

//import java.lang.*;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.*;

import static java.lang.Integer.toHexString;

public class Controller implements Initializable {
    public Button assembleButton;
    public Button executeButton;
    public Button resetButton;
    public Button aboutButton;
    public Button stepButton;

    public TextArea hexdumpDisplay;
    public TextArea inputText;
    public TextArea terminal;
    public TextArea registerDisplay;

    private static Register A = new Register();
    private static Register X = new Register();
    private static Register Y = new Register();
    private static FLAGRegister registerFlag = new FLAGRegister();
    private static bigRegister stackPointer = new bigRegister();
    private static bigRegister programCounter = new bigRegister();

    private static byte[] mem; //= memInit(); //<-- initialise to completely zero values   //changed to byte from int

    public static int PC = 0;     //program should begin at address 0
    public static int PCIndex = PC;   //this provides an offset variable for the assembly stage
    public String inputBuf;
    public List<Label> labelList = new ArrayList<>();     //a list of the label objects

    @Override
    public void initialize(URL location, ResourceBundle resources){
        memInit();
        registerInit();
    }

    //TODO: Consider configuring this into an Android app (would need to redo the GUI), however the code written in Java would largely remain untouched!

    public void assembleClick(){
        System.out.println("assemble!");

        //reset memory and execution starting point
        memInit();
        PCIndex = 0;

        //initialise the registers
        registerInit();

        //status update the terminal
        terminal.setText("Assembling...");

        //create a buffer of the user's input
        inputBuf = inputText.getText();

        //now append the BRK keyword to the end so that the assembler method knows when to exit...
        inputBuf = appendBRKinstruction(inputBuf);

        //debugging
        //System.out.println(inputBuf);



        //TODO: implement some illegal opcodes???
        //assemble the assembly! (convert the inputBuf assembly into mem[] machine code)
        Assemble.assemble(mem, labelList, inputBuf, A, X, Y, programCounter, registerFlag, stackPointer, hexdumpDisplay, registerDisplay, terminal);

        for(int i = 0; i < labelList.size(); i++){
            System.out.println("Name: " + labelList.get(i).name + " Address: " + labelList.get(i).address);
        }

        //this overwrites the "Assembling..." text, must instead append...
        terminal.setText("Program Assembled");

        //debugging
        for(int i = 0; i < 20; i++){
            System.out.println(i + " : " + toHexString(mem[i]));
        }


        /* TODO give the terminal more meaningful output, print out the found labels with their names and respective addresses
           as well as the amount of memory the program is contained in (given by PCIndex(?))
        */
    }

    //execute the machine code in the mem[] array in its entirety
    public void executeClick(){ //String[] memString){
        System.out.println("execute!");
        terminal.setText("Executing Program");

        boolean step = false;
        Execute.execute(mem, step, A, X, Y, registerFlag, stackPointer, programCounter, hexdumpDisplay, registerDisplay, terminal);
    }

    //execute the next instruction and then return
    public void stepClick(){
        System.out.println("step!");

        boolean step = true;
        Execute.execute(mem, step, A, X, Y, registerFlag, stackPointer, programCounter, hexdumpDisplay, registerDisplay, terminal);
    }

    //simply reset the memory and registers
    public void resetClick(){
        System.out.println("reset!");

        /*for(int i = 0; i < mem.length; i++){
            mem[i] = 0;
        }*/

        registerInit();  //set all the registers to their initial values
        //A.val = X.val = Y.val = 0;
        //programCounter.val = 0;
        //stackPointer.val = 0x100;

        //registerFlag.N=registerFlag.Z=registerFlag.C=registerFlag.I=registerFlag.D=registerFlag.V = 0;
        //PC = 0;
        labelList.clear();

        updateDisplays(mem, hexdumpDisplay, registerDisplay, A, X, Y, registerFlag, programCounter);
    }

    //give some information about the program...
    public void aboutClick(){
        //mention any annoying syntax quirks about this emulator

        System.out.println("about!");
    }

    /*
    //TODO: a memory mapped screen that can have pictures drawn on it by writing to certain memory locations
    static void updateScreen(int[] mem){

    }
    */

    //TODO: currently the last column of memory is not displayed... addresses ending in F are not shown!!!!
    //update the register and memory hexdump displays with the contents of the mem[] array
    static void updateDisplays(byte[] mem, TextArea hexdumpDisplay, TextArea registerDisplay, Register A, Register X,
                               Register Y, FLAGRegister registerFlag, bigRegister programCounter){

        int i,j;
        StringBuilder hdisplay = new StringBuilder();
        StringBuilder rdisplay = new StringBuilder();

        //construct the output for the hexdump display
        for(i = 0; i < mem.length; i++){
            if(i == 0)
                hdisplay.append("00" + ": ");
            else
                hdisplay.append(toHexString(i).toUpperCase() + ": ");


            for(j = 0; j < 31; j++,i++){
                if(mem[i] == 0)
                    hdisplay.append("00" + "  ");
                else
                    hdisplay.append(toHexString(mem[i]).toUpperCase() + "  ");
            }
            hdisplay.append("\n");
        }
        //this is the hexdump display string
        String hexdumpDisp = hdisplay.toString();

        //construct the output for the register display
        rdisplay.append("A:  " + toHexString(A.val).toUpperCase() + "\n");
        rdisplay.append("X:  " + toHexString(X.val).toUpperCase() + "\n");
        rdisplay.append("Y:  " + toHexString(Y.val).toUpperCase() + "\n");
        rdisplay.append("SP: " + toHexString(stackPointer.val).toUpperCase() + "\n");
        rdisplay.append("PC: " + toHexString(programCounter.val).toUpperCase() + "\n");
        rdisplay.append("Flags: N Z C I D V\n" +"           "+registerFlag.N+' '+registerFlag.Z+' '+registerFlag.C+' '+registerFlag.I
                +' '+registerFlag.D+' '+registerFlag.V);

        String registerDisp = rdisplay.toString();

        registerDisplay.setText(registerDisp);
        hexdumpDisplay.setText(hexdumpDisp);
    }


    //return an all zero string of ints
    private void memInit(){
        mem = new byte[4096];
        for(int i = 0; i < 4095; i++){
            mem[i] = 0x0;
        }
    }

    private void registerInit(){
        A.val = 0; X.val = 0; Y.val = 0; stackPointer.val = 0x100; PC = 0; programCounter.val = 0;

        registerFlag.N = 0; registerFlag.Z = 0; registerFlag.C = 0;
        registerFlag.I = 0; registerFlag.D = 0; registerFlag.V = 0;
    }

    //append the BRK_ instruction to the inputBuf
    private String appendBRKinstruction(String inputBuf) {
        return inputBuf = inputBuf.concat("\nBRK__");   //two underscores!
    }
    
}
