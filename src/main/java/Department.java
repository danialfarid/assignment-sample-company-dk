import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Collection;

@Entity
public class Department {

    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;
    private String name;
    @OneToMany(mappedBy="department")
    private Collection<Employee> employees;

    @NotNull
    private String address;
    @NotNull
    private String city;
    @NotNull
    private String country;
    private String email;
    private String phone;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String deptName) {
        this.name = deptName;
    }

    public void setEmployees(Collection<Employee> employees) {
        this.employees = employees;
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

    public void addEmployee(Employee employee) {
        if (!employees.contains(employee)) {
            employees.add(employee);

        }
    }

    public Collection<Employee> getEmployees() {
        return employees;
    }

    @Override
    public String toString() {
        return "Department [employees=" + employees + ", id=" + id + ", name="
                + name + "]";
    }
}