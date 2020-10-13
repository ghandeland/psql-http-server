package no.kristiania.http;

import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ProjectMemberDao {

    public List<ProjectMember> projectMemberList;
    private DataSource dataSource;

    public ProjectMemberDao(DataSource dataSource) {
        this.dataSource = dataSource;
        projectMemberList = new ArrayList<>();
    }

    public List<ProjectMember> getProjectMemberList() {
        return projectMemberList;
    }

    public static void main(String[] args) throws SQLException {

        // Initialize PGSimpleDataSource
        PGSimpleDataSource pgDataSource = new PGSimpleDataSource();
        pgDataSource.setUrl("jdbc:postgresql://localhost:5432/project");
        pgDataSource.setUser("oppgavesett08");
        pgDataSource.setPassword("nw3fGmA9nKgbwtGwpj");

        // Prompt value
        System.out.println("Enter value to insert:");
        Scanner scanner = new Scanner(System.in);
        String projectMemberName = scanner.nextLine();

        // Insert into value DB
        try (Connection connection = pgDataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("insert into project_members (name) " + "values (?)")) {
                statement.setString(1, projectMemberName);
                statement.executeUpdate();
            }
        }

        //Print all values
        try (Connection connection = pgDataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from project_members")) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    List<ProjectMember> projectMemberList = new ArrayList<>();

                    while(resultSet.next()) {
                        int id = resultSet.getInt("id");
                        String name = resultSet.getString("name");
                        String role = resultSet.getString("role");

                        System.out.println("#" + id + ": " + name);
                    }
                }
            }
        }

    }

    public List<ProjectMember> listAll() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from project_members")) {
                try (ResultSet resultSet = statement.executeQuery()) {
                    projectMemberList = new ArrayList<>();

                    while(resultSet.next()) {
                        ProjectMember projectMember = new ProjectMember();
                        projectMember.setId(resultSet.getInt("id"));
                        projectMember.setName(resultSet.getString("name"));
                        projectMember.setRole(resultSet.getString("role"));

                        projectMemberList.add(projectMember);
                    }

                    return projectMemberList;
                }
            }
        }
    }

    public void insert(String name) throws SQLException {
        /*try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("insert into project_members (name) " + "values (?)")) {
                statement.setString(1, projectMemberName);
                statement.executeUpdate();
            }
        }
        projectMemberList.add(new ProjectMember(name));*/
        insert(new ProjectMember(name));
    }

    public void insert(String name, String role) throws SQLException {
        insert(new ProjectMember(name, role));
    }

    public void insert(ProjectMember projectMember) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("insert into project_members (name, role) " + "values (?, ?)", Statement.RETURN_GENERATED_KEYS)) {
                statement.setString(1, projectMember.getName());
                statement.setString(2, projectMember.getRole());

                statement.execute();

                try(ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if(generatedKeys.next()) {
                        projectMember.setId(generatedKeys.getInt(1));
                    }
                }
            }
        }
        projectMemberList.add(projectMember);
    }

    public void printAll() throws SQLException {
        System.out.println("ProjectMemberDao.printAll() ---");
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from project_members")) {
                try (ResultSet rs = statement.executeQuery()) {
                    while (rs.next()) {
                        System.out.println(rs.getInt("id") + " - " + rs.getString("name") + " - " + rs.getString("role"));
                    }
                }
            }
        }
    }


    public List<String> listNames() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from project_members")) {
                try (ResultSet rs = statement.executeQuery()) {
                    List<String> projectNames = new ArrayList<>();

                    while (rs.next()) {
                        projectNames.add(rs.getString("name"));
                    }
                    return projectNames;
                }
            }
        }
    }

    public List<String> listRoles() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from project_members")) {
                try (ResultSet rs = statement.executeQuery()) {
                    List<String> projectRoles = new ArrayList<>();

                    while (rs.next()) {
                        projectRoles.add(rs.getString("role"));
                    }
                    return projectRoles;
                }
            }
        }
    }

    public int getUnusedId() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from project_members")) {
                try (ResultSet rs = statement.executeQuery()) {
                    int biggestId = Integer.MIN_VALUE;

                    while(rs.next()) {
                        int idInt = rs.getInt("id");
                        if(idInt > biggestId) { biggestId = idInt; }
                    }

                    return biggestId + 1;
                }
            }

        }
    }

    public ProjectMember retrieve(int id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from project_members where id = ?")) {
                statement.setInt(1, id);
                try (ResultSet rs = statement.executeQuery()) {
                    ProjectMember projectMember = new ProjectMember();

                    while (rs.next()) {
                        projectMember.setId(rs.getInt("id"));
                        projectMember.setName(rs.getString("name"));
                        projectMember.setRole(rs.getString("role"));
                    }
                    return projectMember;
                }
            }
        }
    }
}
