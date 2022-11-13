/* Agent Station environment for static and mobile software agents
 * Copyright (C) 2022  Dr Christos Bohoris
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 * connectina.co.uk/agent-station
 */
package uk.co.connectina.agentstation.api.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for the JSONConverter class.
 *
 * @author Dr Christos Bohoris
 */
class JSONConverterTest {

    private String employeeJson = "{\"id\":106,\"name\":\"Chris Bell\",\"permanent\":false,\"address\":{\"street\":\"Peaceful Street\",\"city\":\"Guildford\",\"postCode\":\"GU20 5TZ\"},\"phoneNumbers\":[77882,77883],\"role\":\"Manager\",\"cities\":[\"London\",\"Paris\"],\"properties\":{\"salary\":\"50000 Eur\",\"age\":\"38 years\"}}";
    private String employeePrettyJson = """
            {
              "id" : 106,
              "name" : "Chris Bell",
              "permanent" : false,
              "address" : {
                "street" : "Peaceful Street",
                "city" : "Guildford",
                "postCode" : "GU20 5TZ"
              },
              "phoneNumbers" : [ 77882, 77883 ],
              "role" : "Manager",
              "cities" : [ "London", "Paris" ],
              "properties" : {
                "salary" : "50000 Eur",
                "age" : "38 years"
              }
            }""";
    private String employeeToString = "Employee{id=106, name=Chris Bell, permanent=false, address=Address{street=Peaceful Street, city=Guildford, postCode=GU20 5TZ}, phoneNumbers= 77882 77883, role=Manager, cities=[London, Paris], properties={salary=50000 Eur, age=38 years}}";

    @Test
    void jsonToObject() throws OperationException {
        JSONConverter<Employee> converter = new JSONConverter<>(Employee.class);
        Employee employee = converter.jsonToObject(employeeJson);
        Assertions.assertEquals(employeeToString, employee.toString());
    }

    @Test
    void objectToJson() throws OperationException {
        JSONConverter<Employee> converter = new JSONConverter<>(Employee.class);
        String json = converter.objectToJson(createEmployee());
        Assertions.assertEquals(employeeJson, json);
    }

    @Test
    void objectToPrettyFormatJson() throws OperationException {
        JSONConverter<Employee> converter = new JSONConverter<>(Employee.class);
        String json = converter.objectToPrettyFormatJson(createEmployee());
        Assertions.assertEquals(employeePrettyJson, json);
    }

    public Employee createEmployee() {

        Employee emp = new Employee();
        emp.setId(106);
        emp.setName("Chris Bell");
        emp.setPermanent(false);
        emp.setPhoneNumbers(new long[]{77882, 77883});
        emp.setRole("Manager");

        Address add = new Address();
        add.setCity("Guildford");
        add.setStreet("Peaceful Street");
        add.setPostCode("GU20 5TZ");
        emp.setAddress(add);

        List<String> cities = new ArrayList<>();
        cities.add("London");
        cities.add("Paris");
        emp.setCities(cities);

        Map<String, String> props = new HashMap<>();
        props.put("salary", "50000 Eur");
        props.put("age", "38 years");
        emp.setProperties(props);

        return emp;
    }

}

class Address {

    private String street;
    private String city;
    private String postCode;

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    @Override
    public String toString() {
        return "Address{" + "street=" + street + ", city=" + city + ", postCode=" + postCode + '}';
    }

}

class Employee {

    private int id;
    private String name;
    private boolean permanent;
    private Address address;
    private long[] phoneNumbers;
    private String role;
    private List<String> cities;
    private Map<String, String> properties;

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

    public boolean isPermanent() {
        return permanent;
    }

    public void setPermanent(boolean permanent) {
        this.permanent = permanent;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public long[] getPhoneNumbers() {
        return phoneNumbers;
    }

    public void setPhoneNumbers(long[] phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<String> getCities() {
        return cities;
    }

    public void setCities(List<String> cities) {
        this.cities = cities;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {
        StringBuilder phoneBuilder = new StringBuilder();
        for (long phone : phoneNumbers) {
            phoneBuilder.append(" ");
            phoneBuilder.append(phone);
        }

        return "Employee{" + "id=" + id + ", name=" + name + ", permanent=" + permanent + ", address=" + address + ", phoneNumbers=" + phoneBuilder.toString() + ", role=" + role + ", cities=" + cities + ", properties=" + properties + '}';
    }

}
