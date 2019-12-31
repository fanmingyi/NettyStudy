import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Demo {
    public static void main(String[] args) {
        LocalDate date = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("YYYY MM dd");
        String text = date.format(formatter);
//        LocalDate parsedDate = LocalDate.parse(text, formatter);

        System.out.println(text);
    }
}
