package com.epam.ld.javabasics30.unit14.skils;

import java.util.Date;
import java.util.Objects;

public class Author {
    private String name;
    private String surname;
    private String middlename;
    private Date birthday;

    public Author(String name, String surname, String middlename, Date birthday) {
        this.name = name;
        this.surname = surname;
        this.middlename = middlename;
        this.birthday = birthday;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Author author = (Author) o;
        return surname.equals(author.surname) &&
                name.equals(author.name) &&
                middlename.equals(author.middlename) &&
                birthday.equals(author.birthday);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname, middlename, birthday);
    }

    @Override
    public String toString() {
        return "Author{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", middlename='" + middlename + '\'' +
                ", birthday=" + birthday +
                '}';
    }
}
