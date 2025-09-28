package main.jpa.relations;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;

import entities.Department;
import entities.Employee;
import entities.Meeting;
import entities.ParkingLot;
import jakarta.persistence.EntityManager;
import utils.JpaUtils;

public class Main {
	public static void main(String[] args) {
		JpaUtils.withDbTransaction("testpu", em -> {
			int hrId       = createHrDepartment(em);
			int financesId = createFinancesDepartment(em);

			addEmployeesWithLotsIntoDepartments(em, hrId, financesId);
			// Associate employees with a meeting entity
			organizeMeeting(em);
			// Print out some query results to observe the behavior
			queryResults(em, hrId);
		});
	}

	private static int createFinancesDepartment(EntityManager em) {
		Department finances = new Department();
		finances.setName("Finances");
		em.persist(finances);

		return finances.getId();
	}

	private static int createHrDepartment(EntityManager em) {
		Department hr = new Department();
		hr.setName("Human Resources");
		em.persist(hr);
		return hr.getId();
	}

	private static void addEmployeesWithLotsIntoDepartments(EntityManager em, int hrId, int financesId) {
		// Get departments
		Department hr = em.find(Department.class, hrId);

		// The getReference method does not fetch the entity from the database,
		// it just returns a proxy, which can be used to create associations,
		// or to lazily load the entity data
		Department finances = em.getReference(Department.class, financesId);

		for (int i = 0; i < 5; ++i) {
			addEmployeeToHr(em, hr, i);
		}

		for (int i = 0; i < 10; ++i) {
			addEmployeeToFinances(em, finances, i);
		}
	}

	private static void addEmployeeToFinances(EntityManager em, Department finances, int i) {
		var emp = new Employee();
		emp.setName("Finances worker #" + i);
		emp.setDepartment(finances);
		finances.addEmployee(emp);
		em.persist(emp);
	}

	private static void addEmployeeToHr(EntityManager em, Department hr, int i) {
		var emp = createEmployee(hr, i);

		// HR employees also have a parking lot
		var pl = new ParkingLot("Building A, slot #" + i, emp);
		emp.setParkingLot(pl);

		// We don't need to persist ParkingLot separately, see cascadeType
		em.persist(emp);
	}

	private static Employee createEmployee(Department dept, int i) {
		var emp = new Employee("HR worker #" + i, dept);
		dept.addEmployee(emp);
		return emp;
	}

	private static void organizeMeeting(EntityManager em) {
		var hrWorkers = em.createQuery("""
			SELECT e
			FROM Employee e
			WHERE e.department.name = 'Human Resources'
			""",
        		Employee.class).getResultList();

		var start = new GregorianCalendar(2013, 01, 20, 12, 45);
		var finish = new GregorianCalendar(2013, 01, 20, 14, 15);
		var hrMeeting = new Meeting("HR meeting", start, finish);
		em.persist(hrMeeting);

		for (Employee e : hrWorkers) {
			hrMeeting.addEmployee(e);
			e.addMeeting(hrMeeting);
		}
	}

	private static void queryResults(EntityManager em, int hrId) {
		// Force to send pending queries to the database
		em.flush();
		// Clear the internal entity collection (forces data re-fetch)
		em.clear();

		// Select the name of each department, and the count of employees in them
		List<Object[]> deptStat = em.createQuery(
		        "SELECT d.name, COUNT(e) FROM Department d JOIN d.employees e GROUP BY d.name",
		        Object[].class).getResultList();

		for (Object[] data : deptStat) {
			System.out.printf("Department name: %s, employee count: %s%n",
				data[0], data[1]);
		}

		// Select the employees from the HR department
		// Note that these should be ordered descending by name (@OrderBy)
		List<Employee> hrEmps = em.find(Department.class, hrId).getEmployees();

		for (Employee e : hrEmps) {
			System.out.printf("%s, parking lot: %s, meetings: ",
			        e.getName(), e.getParkingLot().getLocation());

			// List all meetings
			var sdf = new SimpleDateFormat();
			for (Meeting m : e.getMeetings()) {
				var startTime  = sdf.format(m.getStart().getTime());
				var finishTime = sdf.format(m.getFinish().getTime());
				System.out.printf("\t%s (%s - %s)", m.getTitle(), startTime, finishTime);
			}

			System.out.println();
		}
	}
}
