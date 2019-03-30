import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

public class LinearSystemsSolver {

    public static void main(String[] args) throws FileNotFoundException, IOException {

        //taking user input
        Scanner kboard = new Scanner(System.in);

        boolean validInput=false;
        String input;
        String filename;
        String[] inputs;//confirming that user input is valid
        do {
            System.out.println("enter a valid command i.e('gaussian filename.lin', or 'gaussian --spp filename.lin')");
           input= kboard.nextLine();
           inputs= input.split(" ");
           if(inputs[0].equals("gaussian")){
               validInput=true;

            }

        }while(!validInput);

            if (inputs[1].equals("--spp")) {
                filename = inputs[2];
            } else {
                filename = inputs[1];
            }

            //reading the file system

       // System.out.println(Arrays.toString(inputs));
        File file= new File("src/"+filename);
        Scanner filereader = new Scanner(file);

        //parsing and placing file input into correct fields
        int size=0;
        double[][] coefficients= new double[1][1];
        double[] solutions= new double[1];
        int counter=0;
        int coefficientsCounter=0;
        do{
            String currentLine=filereader.nextLine();
            if (counter==0){
                size=Integer.parseInt(currentLine);
               // System.out.println(size);
                counter++;
                coefficients= new double[size][size];

            }
            else{

                String[] lineArray=currentLine.split("\\s+");
                double[] currentDouble= new double[size];
                for(int i=0;i<lineArray.length;i++){
                    currentDouble[i]=Double.parseDouble(lineArray[i]);

                }
                if(coefficientsCounter==size){
                    solutions=currentDouble;
                }
                else {
                    //System.out.println(Arrays.toString(coefficients[counter-1]));
                    coefficients[coefficientsCounter] = currentDouble;
                    counter++;
                    coefficientsCounter++;
                }

            }

        }while (filereader.hasNext());

        //choosing spp, or not
        String filePath="";
        String fileContent="";
        if(inputs[1].contains("spp")){

            solutions=sppGaussian(coefficients,solutions);
             fileContent= Arrays.toString(solutions);
            filePath= inputs[2];

        }
        else{

            solutions=naiveGaussian(coefficients,solutions);
             fileContent= Arrays.toString(solutions);
            filePath= inputs[1];

        }
        StringBuilder sb= new StringBuilder(filePath);
        sb.replace(filePath.length()-3,filePath.length(),"sol");
        filePath=sb.toString();
       // System.out.println(filePath);
        BufferedWriter bw= null;
        FileWriter fw = null;
        try{
            fw= new FileWriter(filePath);
            bw= new BufferedWriter(fw);
            bw.write(fileContent);

        }catch (IOException e){

        }finally {
            try{
                if(bw!=null){
                    bw.close();
                }
                if(fw!=null){
                    fw.close();
                }
            }catch (IOException e){

            }
        }


    }

    private static double[] naiveGaussian  (double[][] coefficients, double[] solutions){

        int size = coefficients.length;
        //calling forward elimination
        for(int i = 0 ; i < size; i++){
            for (int k = i + 1; k < size; k++){
                double scale = -(coefficients[k][i])/(coefficients[i][i]);

                for(int j = i; j < size; j++){
                    if (i == j){
                        coefficients[k][j] = 0;
                    }
                    else{
                        coefficients[k][j] += scale * coefficients[i][j];
                    }

                }
                solutions[k] += scale * solutions[i];
            }
        }

        return backSubstitution(coefficients, solutions);//calling back sub
    }
    public static double[] backSubstitution(double[][] coefficients, double[] solution){

        double[] answer = new double[coefficients.length];

        for (int i = coefficients.length - 1; i >= 0; i--) {
            double sum = 0.0;
            for (int j = i + 1; j < coefficients.length; j++) {
                sum += coefficients[i][j] * answer[j];
            }
            answer[i] = (solution[i] - sum) / coefficients[i][i];
        }


        return answer;
    }

    public static double[] sppGaussian(double[][] coeefficients, double[] solution ){
        int[] order=  new int[coeefficients.length];
        int size= coeefficients.length;


        // Initialize index and scaling vectors

        for(int i =0;i<size;i++){
            order[i]=i;
        }



        //SPPFwdElimination

        double[] scaling = new double[size];

        for (int i =0; i< size;i++){
            double smax=0;
            for( int j=0; j<size;j++){// find coefficient with greatest absolute value
                if(smax<coeefficients[i][j]){
                    smax=coeefficients[i][j];
                }
            }
            scaling[i]=smax;
        }

        for(int k=0; k<size-1;k++){//loop till last column
            double rmax=0;
            int maxIndex=k;
            for(int i=k;i<size;i++){
                double r=coeefficients[order[i]][k]/scaling[order[i]];
                if(r>rmax){
                    rmax=r;
                    maxIndex=i;
                }
            }
            int temp= order[maxIndex];//swap indexes
            order[maxIndex]=order[k];
            order[k]=temp;
            for(int i=k+1;i<size;i++){
                double mult=coeefficients[order[i]][k]/coeefficients[order[k]][k];
                for(int j=k+1;j<size;j++){
                    coeefficients[order[i]][j]=coeefficients[order[i]][j]-mult*coeefficients[order[k]][j];
                }
                solution[order[i]]= solution[order[i]]-mult*solution[order[k]];
            }
        }


        return sppBackSub(coeefficients,solution,order);
    }

    public static double[] sppBackSub(double[][]coeefficients, double[]solution, int[]order){

        double[] answer = new   double[coeefficients.length];
        int last= order[order.length-1];
        //computing the last element;

        answer[answer.length-1]= solution[last]/coeefficients[last][coeefficients[last].length-1];
        for(int i=order.length-2;i>=0;i --){
            double sum=0;
            int currentRow= order[i];
            for(int j=i+1; j<coeefficients[i].length;j++){
                sum+=coeefficients[currentRow][j]*answer[j];
            }
            answer[i]=(solution[currentRow]-sum)/coeefficients[currentRow][i];
        }

        return answer;
    }



    }

