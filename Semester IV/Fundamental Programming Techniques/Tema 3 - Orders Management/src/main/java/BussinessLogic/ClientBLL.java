package BussinessLogic;

import BussinessLogic.Validators.EmailValidator;
import BussinessLogic.Validators.Validator;
import DataAccess.ClientDAO;
import Model.Client;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class ClientBLL {

    private List<Validator<Client>> validators;

    public ClientBLL() {
        validators = new ArrayList<>();
        validators.add((Validator<Client>) new EmailValidator());
    }

    public Client findClientById(int id) {
        Client client = ClientDAO.findById(id);
        if (client == null) {
            throw new NoSuchElementException("The client with id =" + id + " was not found!");
        }
        return client;
    }

    public int insertClient(Client client) {
        for (Validator<Client> v : validators) {
            v.validate(client);
        }
        ClientDAO.insert(client);
        return 1;
    }

    public List<Client> getAllClients() {
        return ClientDAO.findAll();
    }
}