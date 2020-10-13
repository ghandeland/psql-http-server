package no.kristiania.http;

import java.util.Random;

public class SampleData {

    public ProjectMember sampleProjectMember() {
        return new ProjectMember(samplePersonName(), sampleRole());
    }

    public String samplePersonName() {
        String[] namesArray = {"Ray Roth", "Adrienne Schmidt", "Wallace Hood", "Fuller Hurst", "Lane Vang", "Norman Cabrera", "Vielka Booth", "Laura Duffy", "Seth Petersen", "Carla Roberson", "Wilma Gallagher", "Robert Soto", "Lee Richmond", "Damon Wilkerson", "Leandra Cooper", "Daryl Banks", "Donovan Durham", "Jermaine Haney", "Joseph Barber", "Silas Cline", "Thomas West"};
        Random random = new Random();
        return namesArray[random.nextInt(namesArray.length)];
    }

    public String sampleRole() {
        String[] roleArray = {"Architect", "Designer", "Manager", "HR", "UX", "Developer", "PR", "Engineering"};
        Random random = new Random();
        return roleArray[random.nextInt(roleArray.length)];
    }
}
