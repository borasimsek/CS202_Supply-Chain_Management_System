package org.ozyegin.cs.repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.sql.DataSource;

import org.ozyegin.cs.entity.Company;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

@Repository
public class CompanyRepository extends JdbcDaoSupport {
    final String getNamePS = "SELECT * FROM Company WHERE cName = ?";
    final String eMailPS = "SELECT cEmail FROM cEmail WHERE cName = ?";
    final String cityZipPS = "SELECT city FROM City WHERE zip = ?";
    final String nameCountryPS = "SELECT cName FROM Company WHERE country = ?";
    final String deleteNamePS = "DELETE FROM Company WHERE cName = ?";
    final String deleteAllPS = "DELETE FROM Company";
    final String cityCreatePS = "INSERT INTO City (zip, city) VALUES (?, ?)";
    final String companyCreatePS = "INSERT INTO Company (cName, country, zip, street, cPhoneNumber) VALUES (?, ?, ?, ?, ?)";
    final String eMailCreatePS = "INSERT INTO cEmail (cName, cEmail) VALUES (?,?)";

    @Autowired
    public void setDatasource(DataSource dataSource) {
        super.setDataSource(dataSource);
    }

    private final class CompanyMapper implements RowMapper<Company> {

        @Override
        public Company mapRow(ResultSet resultSet, int i) throws SQLException {
            Company company = new Company();
            company.setName(resultSet.getString("cName"));
            company.setZip(resultSet.getInt("zip"));
            company.setCountry(resultSet.getString("country"));
            company.setStreetInfo(resultSet.getString("street"));
            company.setPhoneNumber(resultSet.getString("cPhoneNumber"));

            return company;
        }
    }

    private final RowMapper<String> stringRowMapper = (resultSet, i) -> resultSet.getString(1);

    public Company find(String name) {
        Company company = Objects.requireNonNull(getJdbcTemplate()).queryForObject(getNamePS, new Object[]{name}, new CompanyMapper());
        company.setE_mails(Objects.requireNonNull(getJdbcTemplate()).query(eMailPS,
                new Object[]{company.getName()}, stringRowMapper));
        List<String> cityQuery = Objects.requireNonNull(getJdbcTemplate()).query(cityZipPS, new Object[]{company.getZip()}, stringRowMapper);
        if (cityQuery.size() < 1) {
            company.setCity("");
        } else {
            company.setCity(cityQuery.get(0));
        }
        return company;
    }


    public List<Company> findByCountry(String country) {
        List<String> names = Objects.requireNonNull(getJdbcTemplate()).query(nameCountryPS,
                new Object[]{country}, stringRowMapper);
        ArrayList<Company> company = new ArrayList<>();
        for (String cName : names) {
            company.add(find(cName));
        }
        return company;
    }

    public String create(Company company) throws Exception {
        try {
            String companyCity = Objects.requireNonNull(getJdbcTemplate()).
                    queryForObject(cityZipPS, new Object[]{company.getZip()}, stringRowMapper);
            if (!companyCity.equals(company.getCity())) {
                throw new Exception("City values are not the same.");
            }
        } catch (EmptyResultDataAccessException e) {
            Objects.requireNonNull(getJdbcTemplate()).update(cityCreatePS, company.getZip(), company.getCity());
        }

        Objects.requireNonNull(getJdbcTemplate()).update(companyCreatePS, company.getName(), company.getCountry(), company.getZip(), company.getStreetInfo(),
                company.getPhoneNumber());


        for (String cEmail : company.getE_mails()) {
            Objects.requireNonNull(getJdbcTemplate()).update(eMailCreatePS, company.getName(), cEmail);
        }

        return "Company " + company.getName() + " has been added.";
    }

    public String delete(String name) throws Exception {
        if (Objects.requireNonNull(getJdbcTemplate()).update(deleteNamePS, name) != 1) {
            throw new Exception("Company Delete is failed!");
        } else {
            Objects.requireNonNull(getJdbcTemplate()).update(deleteNamePS, name);
            return "Company " + name + " has been deleted.";
        }
    }

    public void deleteAll() {
        Objects.requireNonNull(getJdbcTemplate()).update(deleteAllPS);
    }
}

