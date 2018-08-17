package com.Pract.DatabaseProject;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;

public class DatabaseProject {
	public static void main(String args[]) {
		DatabaseProject project = new DatabaseProject();
		if (project.isEmployeeExists(20L)) {
			System.out.println("EmployeeId Valid");
			HashMap<Long, EmployeeDetails> hashmap = new HashMap<>();
			hashmap = project.getEmployeeDetails(20L);
			project.displayEmployee(hashmap);
		} else {
			System.out.println("EmployeeId Invalid");
		}
	}

	public boolean isEmployeeExists(Long empId) {
		Connection connection = getConnection();
		boolean isEmployeeExists = false;
		String query = "select EmployeeId from employee where EmployeeId=" + empId;
		Statement statement;
		try {
			statement = connection.createStatement();
			ResultSet resultset = statement.executeQuery(query);
			if (resultset.next()) {
				// resultset.getInt(1);
				isEmployeeExists = true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return isEmployeeExists;
	}

	public HashMap<Long, EmployeeDetails> getEmployeeDetails(Long empId) {
		Connection connection = getConnection();
		HashMap<Long, EmployeeDetails> hashmap = new HashMap<>();
		String query = "select EmployeeId, Name, Salary from employee where EmployeeId=" + empId;
		EmployeeDetails employee = new EmployeeDetails();
		try {
			Statement statement = connection.createStatement();
			long numberOfWorkingDays = getNumberOfWorkingDays(empId);
			ResultSet resultset = statement.executeQuery(query);
			while (resultset.next()) {
				employee.setId(resultset.getLong(1));
				employee.setName(resultset.getString(2));
				employee.setSalary(resultset.getDouble(3));
				employee.setNumOfWorkingDays(numberOfWorkingDays);
				hashmap.put(empId, employee);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hashmap;
	}

	public long getNumberOfWorkingDays(Long empId) {
		Connection connection = getConnection();
		long NumberOfWorkingDays = 0L;
		try {
			connection = new DatabaseProject().getConnection();
			CallableStatement stmt = connection.prepareCall("{?=CALL calculateWorkingDays(?)}");
			stmt.registerOutParameter(1, java.sql.Types.INTEGER);
			stmt.setInt(2, 10);
			stmt.execute();
			NumberOfWorkingDays = stmt.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return NumberOfWorkingDays;
	}

	public void displayEmployee(HashMap<Long, EmployeeDetails> hashmap) {
		for (Long i : hashmap.keySet()) {
			EmployeeDetails employee = hashmap.get(i);
			System.out.println("Employee Id is: " + employee.getId() + "\nEmployee Name: "+ employee.getName() + "\nEmployee Salary: " + employee.getSalary() + "\nNumber of Working days: "+employee.getNumOfWorkingDays());
		}
		System.out.println();
	}

	public Connection getConnection() {
		Connection connection = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			try {
				connection = DriverManager.getConnection("jdbc:mysql://localhost" + ":3306/pract_schema", "root",
						"root");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (ClassNotFoundException e) {
			e.getMessage();
		}
		return connection;
	}
}
