import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "owner")
public class OwnerOld implements Serializable {
    @Id
    @GeneratedValue()
    private Long id;
    @NotNull
    private String name;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="COMPANY_ID")
    private CompanyOld company;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Owner{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public CompanyOld getCompany() {
        return company;
    }

    public void setCompany(CompanyOld company) {
        this.company = company;
    }
}
