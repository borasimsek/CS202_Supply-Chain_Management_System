package org.ozyegin.cs.repository;

import java.util.Date;
import java.util.Objects;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionRepository extends JdbcDaoSupport {
    final String createPS = "INSERT INTO Porder (cName, pId, oAmount, oDate)" + "VALUES (?, ?, ?, ?)";
    final String deleteIDPS = "DELETE FROM Porder WHERE orderID = ?";
    final String deleteAllPS = "DELETE FROM Porder";

    @Autowired
    public void setDatasource(DataSource dataSource) {
        super.setDataSource(dataSource);
    }

    public Integer order(String company, int product, int amount, Date createdDate) {
        return Objects.requireNonNull(getJdbcTemplate()).update(createPS, company, product, amount, createdDate);
    }

    public void delete(int transactionId) throws Exception {
        if (Objects.requireNonNull(getJdbcTemplate()).update(deleteIDPS, transactionId) != 1) {
            throw new Exception("Transaction Delete is failed!");
        }
    }

    public void deleteAll() {
        Objects.requireNonNull(getJdbcTemplate()).update(deleteAllPS);
    }
}
