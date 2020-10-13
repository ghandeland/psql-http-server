package no.kristiania.http;

import org.postgresql.ds.PGSimpleDataSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    public static void main(String[] args) throws SQLException {

        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/project");
        dataSource.setUser("oppgavesett08");
        dataSource.setPassword("nw3fGmA9nKgbwtGwpj");

        ProjectMemberDao projectMemberDao = new ProjectMemberDao(dataSource);

        System.out.println("BEFORE INSERTION:");
        projectMemberDao.printAll();
        System.out.println("\r\r");

        SampleData sampleData = new SampleData();

        ProjectMember newMember = sampleData.sampleProjectMember();

        projectMemberDao.insert(newMember);

        System.out.println("Member inserted: " + newMember.getId() + " - " + newMember.getName() + " - " + newMember.getRole() );
        System.out.println("\r\r");
        System.out.println("AFTER INSERTION:");
        projectMemberDao.printAll();
    }


}
