package dto.request;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRequestUpdate {

    @NotBlank(message = "Nama tidak boleh kosong")
    @Size(max = 200, message = "Nama maksimal 200 karakter")
    public String nama;


    @NotBlank(message = "No HP tidak boleh kosong")
    @Size(max = 20, message = "No HP maksimal 20 karakter")
    public String noHp;

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }


    public String getNoHp() {
        return noHp;
    }

    public void setNoHp(String noHp) {
        this.noHp = noHp;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @NotBlank(message = "Password tidak boleh kosong")
    @Size(min = 6, message = "Password minimal 6 karakter")
    public String password;

}
