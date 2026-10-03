package hexlet.code.dto;

/** Общие данные всех страниц: flash-сообщение и его вид (success / danger). */
public class BasePage {

    private String flash;
    private String flashType;

    public String getFlash() {
        return flash;
    }

    public String getFlashType() {
        return flashType;
    }

    public void setFlash(String flash, String flashType) {
        this.flash = flash;
        this.flashType = flashType;
    }
}
