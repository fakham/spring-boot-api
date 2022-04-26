package com.example.demo.dao;

import com.example.demo.model.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository("postgres")
public class PersonDataAccessService implements PersonDao{

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PersonDataAccessService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int insertPerson(UUID id, Person person) {
        String sql = "INSERT INTO person(id, name) VALUES(?, ?)";
        return jdbcTemplate.update(sql, id, person.getName());
    }

    @Override
    public List<Person> selectAllPeople() {
        String sql = "SELECT id, name FROM person";
        List<Person> people = new ArrayList<>();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        for (Map<String, Object> row : rows)
            people.add(new Person(UUID.fromString(row.get("id").toString()), row.get("name").toString()));
        return people;
    }

    @Override
    public Optional<Person> selectPersonById(UUID id) {
        String sql = "SELECT id, name FROM person WHERE id = ?";
        Person person = jdbcTemplate.queryForObject(sql, new Object[]{id}, (rs, i) -> new Person(UUID.fromString(rs.getString("id")), rs.getString("name")));
        return Optional.ofNullable(person);
    }

    @Override
    public int deletePersonById(UUID id) {
        String sql = "DELETE FROM person WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    @Override
    public int updatePersonById(UUID id, Person person) {
        Optional<Person> personMaybe = selectPersonById(id);
        if (personMaybe.isPresent()) {
            String sql = "UPDATE person SET name = ? WHERE id = ?";
            return jdbcTemplate.update(sql,person.getName(), id);
        }
        return 0;
    }
}
