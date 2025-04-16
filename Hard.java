// Account.java
import javax.persistence.*;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    private int id;

    private String name;

    private double balance;

    public Account() {}

    public Account(int id, String name, double balance) {
        this.id = id;
        this.name = name;
        this.balance = balance;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    // toString
    @Override
    public String toString() {
        return "Account {" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", balance=" + balance +
                '}';
    }
}





// Transaction.java
import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int transId;

    private int fromAccount;
    private int toAccount;
    private double amount;

    @Temporal(TemporalType.TIMESTAMP)
    private Date timestamp = new Date();

    public Transaction() {}

    public Transaction(int fromAccount, int toAccount, double amount) {
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.amount = amount;
        this.timestamp = new Date();
    }

    // Getters and Setters

    public int getTransId() {
        return transId;
    }

    public void setTransId(int transId) {
        this.transId = transId;
    }

    public int getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(int fromAccount) {
        this.fromAccount = fromAccount;
    }

    public int getToAccount() {
        return toAccount;
    }

    public void setToAccount(int toAccount) {
        this.toAccount = toAccount;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    // toString
    @Override
    public String toString() {
        return "Transaction {" +
                "transId=" + transId +
                ", fromAccount=" + fromAccount +
                ", toAccount=" + toAccount +
                ", amount=" + amount +
                ", timestamp=" + timestamp +
                '}';
    }
}





// BankService.java
import org.hibernate.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BankService {

    @Autowired
    private SessionFactory sessionFactory;

    @Transactional
    public void transfer(int fromId, int toId, double amount) {
        Session session = sessionFactory.getCurrentSession();

        Account from = session.get(Account.class, fromId);
        Account to = session.get(Account.class, toId);

        if (from.getBalance() < amount) {
            throw new RuntimeException("Insufficient funds");
        }

        from.setBalance(from.getBalance() - amount);
        to.setBalance(to.getBalance() + amount);

        Transaction tx = new Transaction(fromId, toId, amount);
        session.save(tx);
    }
}




// AppConfig.java
@Configuration
@ComponentScan("com.example.banking")
@EnableTransactionManagement
public class AppConfig {

    @Bean
    public SessionFactory sessionFactory() {
        return new Configuration().configure().buildSessionFactory();
    }

    @Bean
    public HibernateTransactionManager txManager() {
        return new HibernateTransactionManager(sessionFactory());
    }
}




// MainApp.java
public class MainApp {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        BankService service = context.getBean(BankService.class);

        try {
            service.transfer(1, 2, 500.0);
            System.out.println("Transaction successful");
        } catch (Exception e) {
            System.out.println("Transaction failed: " + e.getMessage());
        }

        context.close();
    }
}
