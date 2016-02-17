import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "company")
public class Company extends CompanyBase {
    private String address;
    private String city;
    private String country;
    private String email;
    private String phone;

    @OneToMany(mappedBy = "id", cascade = CascadeType.PERSIST)
    private List<Owner> owners;

    public Company() {
    }

    public Company(Long id) {
        setId(id);
    }

    public Company(Long id, String name) {
        super(id, name);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<Owner> getOwners() {
        return owners;
    }

    public void setOwners(List<Owner> owners) {
        this.owners = owners;
    }

    @Override
    public String toString() {
        return "Company{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", owners=" + owners +
                '}';
    }
}
