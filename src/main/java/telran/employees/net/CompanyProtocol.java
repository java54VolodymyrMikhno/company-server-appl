package telran.employees.net;

import java.util.Arrays;
import java.util.stream.Collectors;

import telran.employees.Company;
import telran.employees.Employee;
import telran.employees.Manager;
import telran.net.Protocol;
import telran.net.Request;
import telran.net.Response;
import telran.net.ResponseCode;

public class CompanyProtocol implements Protocol {
    Company company;

    public CompanyProtocol(Company company) {
        this.company = company;
    }

    @Override
    public synchronized Response  getResponse(Request request) {
        String requestType = request.requestType();
        String requestData = request.requestData();
        Response response = null;

        try {
            response = switch(requestType) {
                case "addEmployee" -> addEmployee(requestData);
                case "getEmployee" -> getEmployee(requestData);
                case "removeEmployee" -> removeEmployee(requestData);
                case "getDepartmentBudget" -> getDepartmentBudget(requestData);
                case "getManagersWithMostFactor" -> getManagersWithMostFactor(requestData);
                default -> wrongDataResponse(requestType);
            };
        } catch (Exception e) {
            response = wrongTypeResponse(e.getMessage());
        }
        return response;
    }

    private Response wrongTypeResponse(String message) {
        return new Response(ResponseCode.WRONG_REQUEST_TYPE, message);
    }

    private Response wrongDataResponse(String requestType) {
        return new Response(ResponseCode.WRONG_REQUEST_DATA, requestType);
    }

    private Response getManagersWithMostFactor(String requestData) {
        Manager[] managers = company.getManagersWithMostFactor();
        return new Response(ResponseCode.OK, managersToJSON(managers));
    }

    private String managersToJSON(Manager[] managers) {
        return Arrays.stream(managers)
                .map(Employee::getJSON)
                .collect(Collectors.joining(";"));
    }

    private Response getDepartmentBudget(String department) {
        int budget = company.getDepartmentBudget(department);
        return new Response(ResponseCode.OK, Integer.toString(budget));
    }

    private Response removeEmployee(String requestData) {
        long id = Long.parseLong(requestData);
        Employee employee = company.removeEmployee(id);
        if (employee == null) {
            return new Response(ResponseCode.WRONG_REQUEST_DATA, "Employee not found");
        }
        return new Response(ResponseCode.OK, employee.getJSON());
    }

    private Response getEmployee(String requestData) {
        long id = Long.parseLong(requestData);
        Employee employee = company.getEmployee(id);
        if (employee == null) {
            return new Response(ResponseCode.WRONG_REQUEST_DATA, "Employee not found");
        }
        return new Response(ResponseCode.OK, employee.getJSON());
    }

    private Response addEmployee(String emplJSON) {
        Employee empl = (Employee) new Employee().setObject(emplJSON);
        company.addEmployee(empl);
        return new Response(ResponseCode.OK, "");
    }
}
