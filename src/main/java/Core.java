public class Core {
    public static void main(String[] args) {
        System.out.println("Here!");
    }


    public int getRandom(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public int multiply(int a, int b){
        return a*b;
    }
}
