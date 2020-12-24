package example;

public class Types {
	public static class Employee {
		public int empid;
		public int deptno;
	}
	public static class Department {
		public int deptno;
	}

	public static class HrSchema {
		public Employee[] emps = new Employee[10];
		public Department[] depts = new Department[10];
	}
}
