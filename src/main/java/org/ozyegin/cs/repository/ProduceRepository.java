package org.ozyegin.cs.repository;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import java.util.Objects;

@Repository
public class ProduceRepository extends JdbcDaoSupport {
    final String createPS = "INSERT INTO Produce (cName, pId, capacity)" + "VALUES (?, ?, ?)";
    final String deleteIDPS = "DELETE FROM Produce WHERE proID = ?";
    final String deleteAllPS = "DELETE FROM Produce";

    @Autowired
    public void setDatasource(DataSource dataSource) {
        super.setDataSource(dataSource);
    }

    public Integer produce(String company, int product, int capacity) {
        return Objects.requireNonNull(getJdbcTemplate()).update(createPS, company, product, capacity);
    }

    public void delete(int produceId) throws Exception {
        if (Objects.requireNonNull(getJdbcTemplate()).update(deleteIDPS, produceId) != 1) {
            throw new Exception("Transaction Delete is failed!");
        } else {
            Objects.requireNonNull(getJdbcTemplate()).update(deleteIDPS, produceId);
        }
    }

    public void deleteAll() {
        Objects.requireNonNull(getJdbcTemplate()).update(deleteAllPS);
    }
}
