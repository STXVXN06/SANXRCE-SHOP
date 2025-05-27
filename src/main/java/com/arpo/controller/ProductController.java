package com.arpo.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.arpo.config.CloudinaryConfig;
import com.arpo.models.CategoryProduct;
import com.arpo.models.Product;
import com.arpo.models.Supplier;
import com.arpo.service.CategoryProductService;
import com.arpo.service.ProductService;
import com.arpo.service.SupplierService;
import com.cloudinary.utils.ObjectUtils;

@Controller
@RequestMapping("/product")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryProductService categoryService;

    @Autowired
    private SupplierService supplierService;

    @Autowired
    private CloudinaryConfig cloudc;

    @GetMapping("/registroProduct")
    public String showformProducts(Model model) {
        List<CategoryProduct> categories = categoryService.listCategory();
        List<Supplier> suppliers = supplierService.listSuppliers();
        model.addAttribute("category", categories);
        model.addAttribute("supplier", suppliers);
        model.addAttribute("product", new Product());
        return "product/add-product";
    }

    @GetMapping("/listProducts")
    public String listProducts(Model model) {
        model.addAttribute("ListProducts", productService.listProduct());
        return "product/listProduct";
    }

    @PostMapping("/saveProduct")
    public String saveProductAndImage(@ModelAttribute Product product, BindingResult result, Model model, @RequestParam("file") MultipartFile file) {

        if (!file.isEmpty() && !file.getContentType().equals("image/jpeg")) {
            model.addAttribute("error", "Solo se permiten archivos de imagen JPEG.");
            List<CategoryProduct> categories = categoryService.listCategory();
            List<Supplier> suppliers = supplierService.listSuppliers();
            model.addAttribute("category", categories);
            model.addAttribute("supplier", suppliers);
            return "product/add-product"; // Vuelve a la vista con el error
        }

        try {
            if (!file.isEmpty()) {
                Map uploadResult = cloudc.upload(file.getBytes(), ObjectUtils.asMap("resourcetype", "auto"));
                if (uploadResult != null && uploadResult.containsKey("url")) {
                    product.setUrlImagen(uploadResult.get("url").toString());
                } else {
                    throw new RuntimeException("Cloudinary no pudo subir la imagen o no devolvió una URL.");
                }
            }

            Long idCategory = product.getIdCategory().getIdCategoryProduct();
            CategoryProduct category = categoryService.getById(idCategory);
            product.setIdCategory(category);

            Long idSupplier = product.getIdSupplier().getIdSupplier();
            Supplier supplier = supplierService.getById(idSupplier);
            product.setIdSupplier(supplier);

            productService.saveProduct(product);
            model.addAttribute("successMessage", "Producto guardado exitosamente.");

            System.out.println("Si GUARDO EL PRODUCTO Y LA IMAGEN");
        } catch (Exception e) {
            System.out.println("Error al guardar producto o subir imagen: " + e.getMessage());
            model.addAttribute("error", "Error al guardar el producto: " + e.getMessage());
            List<CategoryProduct> categories = categoryService.listCategory();
            List<Supplier> suppliers = supplierService.listSuppliers();
            model.addAttribute("category", categories);
            model.addAttribute("supplier", suppliers);
            return "product/add-product";
        }

        return "redirect:/product/registroProduct";
    }

    @GetMapping("/admin/editProduct/{idProduct}")
    public String showUpdateProduct(@PathVariable("idProduct") Long idProduct, Model model) {

        Product product = productService.getByIdProduct(idProduct);

        List<CategoryProduct> categories = categoryService.listCategory();
        List<Supplier> suppliers = supplierService.listSuppliers();

        model.addAttribute("product", product);
        model.addAttribute("categories", categories);
        model.addAttribute("suppliers", suppliers);

        return "product/update-product";
    }

    @PostMapping("/admin/updateProduct/{idProduct}")
    public String updateProduct(@PathVariable("idProduct") Long idProduct, Product product, Model model, @RequestParam("file") MultipartFile file) {
        Product alreadyproducts = productService.getByIdProduct(idProduct);

        if (alreadyproducts != null) {
            alreadyproducts.setNameProduct(product.getNameProduct());
            alreadyproducts.setStock(product.getStock());
            alreadyproducts.setIdCategory(product.getIdCategory());
            alreadyproducts.setIdSupplier(product.getIdSupplier());
            alreadyproducts.setDescription(product.getDescription());
            alreadyproducts.setPrice(product.getPrice());

            if (!file.isEmpty()) {
                if (!file.getContentType().equals("image/jpeg")) {
                    model.addAttribute("error", "Solo se permiten archivos de imagen JPEG.");
                    model.addAttribute("product", alreadyproducts);
                    model.addAttribute("categories", categoryService.listCategory());
                    model.addAttribute("suppliers", supplierService.listSuppliers());
                    return "product/update-product";
                }

                try {
                    Map uploadResult = cloudc.upload(file.getBytes(), ObjectUtils.asMap("resourcetype", "auto"));
                    if (uploadResult != null && uploadResult.containsKey("url")) {
                        alreadyproducts.setUrlImagen(uploadResult.get("url").toString());
                    } else {
                        throw new RuntimeException("Cloudinary no pudo subir la imagen o no devolvió una URL.");
                    }
                } catch (Exception e) {
                    System.out.println("Error al actualizar imagen: " + e.getMessage());
                    model.addAttribute("error", "Error al actualizar la imagen del producto: " + e.getMessage());
                    model.addAttribute("product", alreadyproducts);
                    model.addAttribute("categories", categoryService.listCategory());
                    model.addAttribute("suppliers", supplierService.listSuppliers());
                    return "product/update-product";
                }
            }
            // FIN DE LA MODIFICACIÓN

            productService.saveProduct(alreadyproducts);
            System.out.println("SI LO ACTUALIZO");
            model.addAttribute("successMessage", "El producto ha sido modificado.");

            return "redirect:/product/listProducts";
        }
        return "redirect:/product/listProducts";
    }

    @GetMapping("/admin/deleteProduct/{idProduct}")
    public String deleteProduct(@PathVariable("idProduct") Long idProduct, Model model) {

        Product products = productService.getByIdProduct(idProduct);
        productService.deleteProduct(products.getIdProduct());
        model.addAttribute("product", productService.listProduct());
        return "redirect:/product/listProducts";
    }

    @GetMapping("/productos-disponibles")
    public String getActiveProduct(Model model) {
        ArrayList<Product> products = (ArrayList<Product>) productService.getActiveProducts();
        ArrayList<CategoryProduct> categories = (ArrayList<CategoryProduct>) categoryService.listCategory();
        if (products.isEmpty()) {
            model.addAttribute("noProductsMessage", "No hay productos disponibles en este momento.");
        } else {
            model.addAttribute("products", products);
        }
        model.addAttribute("categories", categories);
        return "/exploreProducts";
    }

    @GetMapping("/categoriaproductos/{nameCategory}")
    public String filterProductsByCategory(@PathVariable("nameCategory") String nameCategory, Model model) {
        List<Product> filteredProducts = (List<Product>) productService.filterProductsByCategory(nameCategory);

        if (filteredProducts.isEmpty()) {
            model.addAttribute("noProductsMessage", "No hay productos disponibles en este momento.");
        } else {
            model.addAttribute("products", filteredProducts);
        }
        model.addAttribute("categories", categoryService.listCategory());

        return "/exploreProductsCategories";
    }

    @GetMapping("/detalleproducto/{idProduct}")
    public String viewDetail(@PathVariable("idProduct") Long idProduct, Model model) {
        Product product = productService.getByIdProduct(idProduct);
        model.addAttribute("product", product);
        return "product/productDetail";
    }

    @PostMapping("/search")
    public String searchProducts(@RequestParam("term") String term, Model model) {
        List<Product> results = productService.searchProductsByName(term);
        if (results.isEmpty()) {
            model.addAttribute("noResults", "No se encontraron productos");
        }
        model.addAttribute("ListProducts", results);
        return "product/listProduct";
    }

}
