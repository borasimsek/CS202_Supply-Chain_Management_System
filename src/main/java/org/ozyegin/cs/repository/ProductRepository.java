package org.ozyegin.cs.repository;

import java.util.*;
import javax.sql.DataSource;

import org.ozyegin.cs.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

@Repository
public class ProductRepository extends JdbcDaoSupport {
    final String getIDPS = "SELECT * FROM Product WHERE id = ?";
    final String getBrandPS = "SELECT * FROM Product WHERE brandName = ?";
    final String deleteAllPS = "DELETE FROM Product";
    final String getMultiplePS = "SELECT * FROM Product WHERE id IN (:ids)";
    final String deleteIDPS = "DELETE FROM Product WHERE id = ?";
    final String createPS = "INSERT INTO Product (name, description, brandName) VALUES(?, ?, ?)";
    final String updatePS = "UPDATE Product SET name=?, description=?, brandName=? WHERE id=?";
    final String IDPS = "SELECT id FROM Product";
    final String getAllIDPS = "SELECT * FROM Product";

    private final RowMapper<Product> ProductRowMapper = (resultSet, i) -> {
        Product pr = new Product();
        pr.id(resultSet.getInt("id"));
        pr.name(resultSet.getString("name"));
        pr.description(resultSet.getString("description"));
        pr.brandName(resultSet.getString("brandName"));

        return pr;
    };

    private final RowMapper<Integer> IntRowMapper = (resultSet, i) -> resultSet.getInt(1);

    @Autowired
    public void setDatasource(DataSource dataSource) {
        super.setDataSource(dataSource);
    }

    public Product find(int id) {
        return Objects.requireNonNull(getJdbcTemplate()).queryForObject(getIDPS, new Object[]{id}, ProductRowMapper);
    }

    public List<Product> findMultiple(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        } else {
            Map<String, List<Integer>> params = new HashMap<>() {
                {
                    this.put("ids", new ArrayList<>(ids));
                }
            };
            var template = new NamedParameterJdbcTemplate(Objects.requireNonNull(getJdbcTemplate()));
            return template.query(getMultiplePS, params, ProductRowMapper);
        }
    }

    public List<Product> findByBrandName(String brandName) {
        return Objects.requireNonNull(getJdbcTemplate()).query(getBrandPS, new Object[]{brandName}, ProductRowMapper);
    }

    public List<Integer> create(List<Product> products) {
        List<Product> createList = (Objects.requireNonNull(getJdbcTemplate()).query(getAllIDPS, ProductRowMapper));

        Objects.requireNonNull(getJdbcTemplate()).batchUpdate(createPS, products,
                products.size(),
                (ps, product) -> {
                    ps.setString(1, product.getName());
                    ps.setString(2, product.getDescription());
                    ps.setString(3, product.getBrandName());
                });
        List<Product> productList = (Objects.requireNonNull(getJdbcTemplate()).query(getAllIDPS, ProductRowMapper));
        productList.removeAll(createList);
        List<Integer> idList = new ArrayList<Integer>();
        for (int i = 0; i < productList.size(); i++) {
            idList.add(productList.get(i).getId());
        }

        return idList;
    }

    public void update(List<Product> products) {
        Objects.requireNonNull(getJdbcTemplate()).batchUpdate(updatePS, products,
                products.size(),
                (ps, product) -> {
                    ps.setString(1, product.getName());
                    ps.setString(2, product.getDescription());
                    ps.setString(3, product.getBrandName());
                    ps.setInt(4, product.getId());
                });
    }

    public void delete(List<Integer> ids) {
        Objects.requireNonNull(getJdbcTemplate()).batchUpdate(deleteIDPS, ids, ids.size(),
                (ps, id) -> {
                    ps.setInt(1, id);
                });
    }

    public void deleteAll() {
        Objects.requireNonNull(getJdbcTemplate()).update(deleteAllPS);
    }
}
