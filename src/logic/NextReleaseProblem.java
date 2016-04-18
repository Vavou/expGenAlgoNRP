/**
 * 
 */
package logic;

import java.util.List;

import org.uma.jmetal.problem.ConstrainedProblem;
import org.uma.jmetal.problem.impl.AbstractGenericProblem;
import org.uma.jmetal.util.solutionattribute.impl.NumberOfViolatedConstraints;

import entities.Employee;
import entities.Priority;
import entities.Task;

/**
 * @author Vavou
 * 
 * Objectives: 
 * 0: doing the most number of tasks
 *
 */
public class NextReleaseProblem extends AbstractGenericProblem<PlanningSolution> implements ConstrainedProblem<PlanningSolution> {

	/* --- Attributes --- */
	
	/**
	 * Generated Id
	 */
	private static final long serialVersionUID = 3302475694747789178L;
	
	/**
	 * Tasks available for the iteration
	 */
	private List<Task> tasks;
	
	/**
	 * Employees available for the iteration
	 */
	private List<Employee> employees;
	
	/**
	 * Number of violated constraints
	 */
	private NumberOfViolatedConstraints<PlanningSolution> numberOfViolatedConstraints;
	
	
	/* --- Constructors --- */

	/**
	 * Constructor
	 * @param tasks tasks of the iteration
	 * @param employees employees available during the iteration
	 */
	public NextReleaseProblem(List<Task> tasks, List<Employee> employees) {
		this.tasks = tasks;
		this.employees = employees;
		
		setNumberOfVariables(1);
		setName("Next Release Problem");
		setNumberOfObjectives(1);
		setNumberOfConstraints(1);
		numberOfViolatedConstraints = new NumberOfViolatedConstraints<PlanningSolution>();
	}
	
	@Override
	public void evaluate(PlanningSolution solution) {
		final int MAX_PRIORITY = tasks.size() * Priority.ONE.getScore();
		int totalScore = 0;
		
		for (int i = 0 ; i < solution.getPlannedTasks().size() ; i++) {
			totalScore += solution.getPlannedTasks().get(i).getTask().getPriority().getScore();
		}
		solution.setObjective(0, MAX_PRIORITY - totalScore);
	}

	/**
	 * @return the tasks
	 */
	public List<Task> getTasks() {
		return tasks;
	}

	/**
	 * @param tasks the tasks to set
	 */
	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

	/**
	 * @return the employees
	 */
	public List<Employee> getEmployees() {
		return employees;
	}

	/**
	 * @param employees the employees to set
	 */
	public void setEmployees(List<Employee> employees) {
		this.employees = employees;
	}

	/**
	 * @return the numberOfViolatedConstraints
	 */
	public NumberOfViolatedConstraints<PlanningSolution> getNumberOfViolatedConstraints() {
		return numberOfViolatedConstraints;
	}

	@Override
	public PlanningSolution createSolution() {
		return new PlanningSolution(this);
	}

	@Override
	public void evaluateConstraints(PlanningSolution solution) {
		int numViolatedConstraints = 0;
		for (int i = 0 ; i < solution.getPlannedTasks().size() ; i++) {
			Task currentTask = solution.getPlannedTasks().get(i).getTask();
			for (Task previousTask : currentTask.getPreviousTasks()) {
				boolean found = false;
				int j = 0;
				while (!found && j < i) { //TODO update condition when we will compare by time and not only by order
					if (solution.getPlannedTasks().get(j).getTask() == previousTask) {
						found = true;
					}
					j++;
				}
				if (!found) {
					numViolatedConstraints++;
				}
			}
		}
		numberOfViolatedConstraints.setAttribute(solution, numViolatedConstraints);
	}
}
