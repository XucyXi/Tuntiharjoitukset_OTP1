package assignments.inclass1;

public class Main {
    public static void main(String[] args) {
        TemperatureConverter converter = new TemperatureConverter();
        System.out.println("Docker Image Execution Success!");
        System.out.println("300 Kelvin on Celsiuksena: " + converter.kelvinToCelsius(300.0));
    }
}