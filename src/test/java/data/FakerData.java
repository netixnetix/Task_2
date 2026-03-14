
package data;
import com.github.javafaker.Faker;
public class FakerData {
    private static final Faker faker = new Faker();
    public static String email() {
        return faker.internet().emailAddress();
    }
    public static String name() {
        return faker.regexify("[А-Яа-я]{4,15}");
    }
    public static String pwd() {
        return faker.internet().password(8, 16, true, true, true);
    }
}