package com.tfg.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tfg.exception.InvalidStockException;
import com.tfg.exception.NullDataException;
import com.tfg.exception.ProductAlreadySoldException;
import com.tfg.repository.ProductRepository;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "CART")
public class ShoppingCartEntity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonFormat(pattern = "dd/MM/yyyy")
	private LocalDate dateCreated;

	@Id
	@Column(name = "ID", length = 50, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// buyer
	@ManyToOne
	private UserEntity user;

	@OneToMany(mappedBy = "cart", fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST}) // cascada
	private List<ProductCartEntity> productCartEntities;

	public ShoppingCartEntity() {
	    this.productCartEntities = new ArrayList<>();
	}
	
	
	public List<ProductCartEntity> getProductCartEntities() {
	  
	    return Collections.unmodifiableList(productCartEntities);
	}
	
	

	public void setProductCartEntities(List<ProductCartEntity> productCartEntities) {
		this.productCartEntities = productCartEntities;
	}



	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public UserEntity getUser() {
		return user;
	}

	public void setUser(UserEntity user) {
		this.user = user;
	}

	public LocalDate getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(LocalDate dateCreated) {
		this.dateCreated = dateCreated;
	}

	public Double getTotalOrderPrice() {
		double sum = 0D;
		for (ProductCartEntity productCartEntity : productCartEntities) {
			ProductEntity product = productCartEntity.getProduct();

			if (product != null) {
	            if (productCartEntity.getQuantityInCart() == 1) {
	                sum += product.getPrice();
	            } else if (productCartEntity.getQuantityInCart() > 1) {
	                sum += product.getPrice() * productCartEntity.getQuantityInCart();
	            }
	        }
		}
		return sum;
	}

	public List<ProductCartEntity> buy(UserEntity user, ProductRepository productRepository) throws ProductAlreadySoldException {
		double totalOrderPrice = getTotalOrderPrice();
		user.decMoney(totalOrderPrice);
		this.user = user;
		this.user._getCarts().add(this);

		for (ProductCartEntity productCartEntity : productCartEntities) {

			ProductEntity product = productCartEntity.getProduct();
			if (product.getStock()>0) {
				product.decStock();
				product.setSold(true);
				productRepository.save(product);
			} else {
				throw new ProductAlreadySoldException("El producto ya ha sido vendido.");
			}
			
		}
		return productCartEntities;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ShoppingCartEntity other = (ShoppingCartEntity) obj;
		return Objects.equals(id, other.id);
	}
	public void incQuantity(ProductEntity prod, Integer quantity) throws InvalidStockException {
	    for (ProductCartEntity productInCart : productCartEntities) {
	        if (productInCart.getProduct().getProductId().equals(prod.getProductId())) {
	            productInCart.incQuantity(prod, quantity);
	            return; 
	        }
	    }
	    ProductCartEntity newProductCart = new ProductCartEntity(prod, this, quantity);
	    addProductCart(newProductCart);
	}

	public void decQuantity(ProductEntity prod, Integer quantity) throws NullDataException {
	    ProductCartEntity toDelete = null;
	    Iterator<ProductCartEntity> iterator = productCartEntities.iterator();
	    
	    while (iterator.hasNext()) {
	        ProductCartEntity productInCart = iterator.next();
	        System.out.println("Current product in cart: " + productInCart.getProduct().getProductId());

	        if (productInCart.getProduct().getProductId().equals(prod.getProductId())) {
	            System.out.println("Found matching product: " + prod.getProductId());

	            int currentQuantity = productInCart.getQuantityInCart();

	            if (currentQuantity > 0) {
	                productInCart.decQuantity(1);

	                if (productInCart.getQuantityInCart() == 0) {
	                    toDelete = productInCart;
	                }
	            }
	        }
	    }

	    if (toDelete != null) {
	        removeProductCart(toDelete);
	    }
	}

	public void addProductCart(ProductCartEntity productCartEntity) {
	    if (productCartEntities == null) {
	        productCartEntities = new ArrayList<>();
	    }
	    this.productCartEntities.add(productCartEntity);
	}


	public void removeProductCart(ProductCartEntity productCartEntity) throws NullDataException {
	    if (productCartEntities != null) {
	        List<ProductCartEntity> mutableList = new ArrayList<>(productCartEntities);
	        
	        mutableList.remove(productCartEntity);

	        productCartEntities = mutableList;
	    } else {
	        throw new NullDataException("productCartEntities is null");
	    }
	}


	public int getQuantityInCart(ProductEntity prod) throws InvalidStockException {
	    for (ProductCartEntity productInCart : productCartEntities) {
	        if (productInCart.getProduct().getProductId().equals(prod.getProductId())) {
	            return productInCart.getQuantityInCart();
	        }
	    }
		return 0;
	   
	}

	
	
	public void clear() {
	    productCartEntities = new ArrayList<>();
	}
}
