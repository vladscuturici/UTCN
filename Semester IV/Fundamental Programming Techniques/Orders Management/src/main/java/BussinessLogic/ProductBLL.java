package BussinessLogic;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import BussinessLogic.Validators.Validator;
import DataAccess.ProductDAO;
import Model.Product;

public class ProductBLL {

    private List<Validator<Product>> validators;

    public ProductBLL() {
        validators = new ArrayList<>();
    }

    public Product findProductById(int id) {
        Product product = ProductDAO.findById(id);
        if (product == null) {
            throw new NoSuchElementException("The product with id =" + id + " was not found!");
        }
        return product;
    }

    public int insertProduct(Product product) {
        for (Validator<Product> v : validators) {
            v.validate(product);
        }
        ProductDAO.insert(product);
        return 1;
    }

    public List<Product> getAllProducts() {
        return ProductDAO.findAll();
    }
}