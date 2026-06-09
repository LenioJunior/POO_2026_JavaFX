module br.edu.ifsuldeminas {
    requires javafx.controls;
    requires javafx.fxml;
    requires spring.context;
    requires spring.beans;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.data.jpa;
    requires spring.tx;
    requires jakarta.persistence;

    opens br.edu.ifsuldeminas to javafx.fxml, spring.core, spring.beans, spring.context;
    opens br.edu.ifsuldeminas.models to jakarta.persistence, spring.core, org.hibernate.orm.core;
    opens br.edu.ifsuldeminas.repositories to spring.core, spring.data.jpa;

    exports br.edu.ifsuldeminas;
}
