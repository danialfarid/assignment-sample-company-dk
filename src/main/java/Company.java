import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Entity
public class Company extends CompanyBase {

    @OneToMany(mappedBy="company")
    private Collection<Owner> owners;

    @NotNull
    private String address;
    @NotNull
    private String city;
    @NotNull
    private String country;
    private String email;
    private String phone;

    public void setOwners(Collection<Owner> owners) {
        this.owners = owners;
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

    public void addEmployee(Owner owner) {
        if (!owners.contains(owner)) {
            owners.add(owner);

        }
    }

    public Collection<Owner> getOwners() {
        return owners;
    }

    @Override
    public String toString() {
        return "Department [employees=" + owners + ", id=" + getId() + ", name="
                + getName()+ "]";
    }
}