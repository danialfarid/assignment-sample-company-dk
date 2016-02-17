import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Entity
@Table(name="company")
public class CompanyBase implements Serializable {
    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    private int id;
    @NotNull
    private String name;

    @OneToMany(mappedBy = "id", cascade = CascadeType.PERSIST)
    private List<OwnerOld> owners;

    public CompanyBase() {
    }

    public CompanyBase(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
