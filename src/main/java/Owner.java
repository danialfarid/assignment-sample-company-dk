import javax.persistence.*;

@Entity
@Table(name = "owner")
public class Owner {
    String name;

    @ManyToOne
    @JoinColumn(name = "STOCK_ID", nullable = false)
    Company company;
}
