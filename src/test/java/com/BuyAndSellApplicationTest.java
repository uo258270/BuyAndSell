package com;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class BuyAndSellApplicationTest {

	@LocalServerPort
	private int port;
//
//	private static Map<String, Object> vars = new HashMap<String, Object>();
//	  static JavascriptExecutor js;
//	
	@Autowired
	private static WebDriver driver;

	@BeforeAll
	public static void setUp() {
		driver = new FirefoxDriver();
	//	js= (JavascriptExecutor) driver;
	}
	
	
	@AfterAll
	public static void tearDown() {
		driver.quit();
	}

	@Test
	public void testLogin() {
		driver.get("http://localhost:" + port + "/login");
		driver.manage().window().setSize(new Dimension(1686, 954));
		driver.findElement(By.id("username")).sendKeys("nuria.sane.r@gmail.com");
		driver.findElement(By.id("password")).sendKeys("nuria1234");
		driver.findElement(By.cssSelector(".btn-primary")).click();

	}

	@Test
	public void testLoginMal() {
		driver.get("http://localhost:" + port + "/login");
		driver.manage().window().setSize(new Dimension(1686, 954));
		driver.findElement(By.id("username")).click();
		driver.findElement(By.id("username")).sendKeys("nuria.sane.r@gmail.co");
		driver.findElement(By.id("content")).click();
		driver.findElement(By.cssSelector(".btn-primary")).click();

	}

	@Test
	public void testLoginVacio() {
		driver.get("http://localhost:" + port + "/login");
		driver.manage().window().setSize(new Dimension(1686, 954));
		driver.findElement(By.cssSelector("#loginForm > .form-group:nth-child(1)")).click();
		driver.findElement(By.cssSelector(".form-group:nth-child(2)")).click();
		driver.findElement(By.id("content")).click();
		driver.findElement(By.cssSelector(".btn-primary")).click();

	}

	
	@Test
	public void registroMalCampoVacio1() {
		driver.get("http://localhost:" + port + "/login");

		driver.manage().window().setSize(new Dimension(1174, 1118));
		driver.findElement(By.cssSelector(".search-button > span")).click();
		driver.findElement(By.cssSelector(".form-horizontal > .form-group:nth-child(1)")).click();
		driver.findElement(By.id("email")).sendKeys("pruebaaaa@gmail.com");
		driver.findElement(By.cssSelector(".form-group:nth-child(4)")).click();
		driver.findElement(By.cssSelector(".btn-primary")).click();
		driver.findElement(By.id("name")).sendKeys("nuria");
		driver.findElement(By.cssSelector(".btn-primary")).click();
		driver.findElement(By.id("lastName")).sendKeys("hhh");
		driver.findElement(By.id("password")).click();
		driver.findElement(By.id("password")).sendKeys("hola");
		driver.findElement(By.id("passwordConfirm")).click();
		driver.findElement(By.id("passwordConfirm")).sendKeys("que tal");
		driver.findElement(By.cssSelector(".btn-primary")).click();
	}

	@Test
	public void registroYaExiste() {
		driver.get("http://localhost:" + port + "/login");
		driver.manage().window().setSize(new Dimension(1174, 1118));
		driver.findElement(By.cssSelector(".search-button > span")).click();
		driver.findElement(By.id("email")).sendKeys("nuria.sane.r@gmail.com");
		driver.findElement(By.id("password")).sendKeys("nuria1234");
		driver.findElement(By.id("name")).click();
		driver.findElement(By.id("name")).sendKeys("nuria");
		driver.findElement(By.id("lastName")).click();
		driver.findElement(By.id("lastName")).sendKeys("nuria");
		driver.findElement(By.id("password")).click();
		driver.findElement(By.id("password")).sendKeys("Nuria");
		driver.findElement(By.id("passwordConfirm")).click();
		driver.findElement(By.id("passwordConfirm")).sendKeys("Nuria");
		driver.findElement(By.cssSelector(".btn-primary")).click();
		driver.findElement(By.id("password")).sendKeys("nuria1234");
	}

	@Test
	public void carritoVer() {
		driver.get("http://localhost:" + port + "/login");
		driver.manage().window().setSize(new Dimension(1174, 1118));
		driver.findElement(By.id("username")).sendKeys("nuria.sane.r@gmail.com");
		driver.findElement(By.id("password")).sendKeys("nuria1234");
		driver.findElement(By.cssSelector(".btn-primary")).click();
		driver.findElement(
				By.cssSelector(".horizontal-list:nth-child(2) > .product-container:nth-child(1) .add-to-cart")).click();
	}
	
	 @Test
	  public void aadirCarritoyDisminuir() {
			driver.get("http://localhost:" + port + "/login");

	    driver.manage().window().setSize(new Dimension(1174, 1118));
	    driver.findElement(By.id("username")).sendKeys("nuria.sane.r@gmail.com");
	    driver.findElement(By.id("password")).sendKeys("nuria1234");
	    driver.findElement(By.cssSelector(".btn-primary")).click();
	    driver.findElement(By.cssSelector(".navbar-right > li:nth-child(2) > a")).click();
	    driver.findElement(By.cssSelector(".product-container:nth-child(1) .glyphicon")).click();
	    driver.findElement(By.cssSelector(".product-container:nth-child(2) .glyphicon")).click();
	    driver.findElement(By.name("action")).click();
	  }

	@Test
	  public void aadirProductoYEditarlo() {
		driver.get("http://localhost:" + port + "/login");

	    driver.manage().window().setSize(new Dimension(1174, 1118));
	    driver.findElement(By.cssSelector(".form-group:nth-child(2) > .col-sm-10")).click();
	    driver.findElement(By.id("username")).click();
	    driver.findElement(By.id("username")).sendKeys("nuria.sane.r@gmail.com");
	    driver.findElement(By.id("password")).sendKeys("nuria1234");
	    driver.findElement(By.cssSelector(".btn-primary")).click();
	    driver.findElement(By.linkText("Gestionar Productos")).click();
	    driver.findElement(By.linkText("Agregar un producto")).click();
	    driver.findElement(By.id("name")).click();
	    driver.findElement(By.id("name")).sendKeys("producto");
	    driver.findElement(By.id("detail")).click();
	    driver.findElement(By.id("detail")).sendKeys("detalle prueba");
	    driver.findElement(By.id("price")).click();
	    driver.findElement(By.id("price")).sendKeys("10");
	    driver.findElement(By.id("stock")).click();
	    driver.findElement(By.id("stock")).sendKeys("3");
	    driver.findElement(By.id("category")).click();
	    {
	      WebElement dropdown = driver.findElement(By.id("category"));
	      dropdown.findElement(By.xpath("//option[. = 'JUGUETES']")).click();
	    }
	    driver.findElement(By.cssSelector(".tagify__input")).click();
	    driver.findElement(By.id("tags")).sendKeys("[{\"value\":\"prueba\"}]");
	    driver.findElement(By.id("tags")).sendKeys("[{\"value\":\"prueba\"},{\"value\":\"prueba2\"}]");
	    driver.findElement(By.id("addProductButton")).click();
	    {
	      WebElement element = driver.findElement(By.cssSelector(".horizontal-list"));
	      Actions builder = new Actions(driver);
	      builder.moveToElement(element).clickAndHold().perform();
	    }
	    {
	      WebElement element = driver.findElement(By.cssSelector(".horizontal-list"));
	      Actions builder = new Actions(driver);
	      builder.moveToElement(element).perform();
	    }
	    {
	      WebElement element = driver.findElement(By.cssSelector(".horizontal-list"));
	      Actions builder = new Actions(driver);
	      builder.moveToElement(element).release().perform();
	    }
	    driver.findElement(By.cssSelector(".product-container:nth-child(14) .edit-product > span")).click();
	    driver.findElement(By.id("stock")).click();
	    driver.findElement(By.id("stock")).sendKeys("19");
	    driver.findElement(By.name("editProductButton")).click();
	  }

	@Test
	public void carritoAadirCantNohaySuficienteStock() {
		driver.get("http://localhost:" + port + "/login");
		driver.manage().window().setSize(new Dimension(1174, 1118));
	    driver.findElement(By.id("username")).sendKeys("nuria.sane.r@gmail.com");
		driver.findElement(By.id("password")).sendKeys("nuria1234");

	    driver.findElement(By.cssSelector(".btn-primary")).click();
	    driver.findElement(By.cssSelector(".horizontal-list:nth-child(2) > .product-container:nth-child(1) .glyphicon")).click();
	    driver.findElement(By.cssSelector(".quantity-container > button:nth-child(3)")).click();

	}
	
	@Test
	  public void buscarunproducto() {
		driver.get("http://localhost:" + port + "/login");
	    driver.manage().window().setSize(new Dimension(1174, 1118));
	    driver.findElement(By.id("username")).sendKeys("nuria.sane.r@gmail.com");
		driver.findElement(By.id("password")).sendKeys("nuria1234");
	    driver.findElement(By.cssSelector(".btn-primary")).click();
	    driver.findElement(By.linkText("Gestionar Productos")).click();
	    driver.findElement(By.linkText("Buscar Producto")).click();
	    driver.findElement(By.cssSelector("form:nth-child(2) #searchTerm")).click();
	    driver.findElement(By.cssSelector("form:nth-child(2) #searchTerm")).sendKeys("gorro");
	    driver.findElement(By.cssSelector(".search-button")).click();
	  }

	 @Test
	  public void buscarPorCategoria() {
			driver.get("http://localhost:" + port + "/login");
	    driver.manage().window().setSize(new Dimension(1174, 1118));
	    driver.findElement(By.id("username")).sendKeys("nuria.sane.r@gmail.com");
		driver.findElement(By.id("password")).sendKeys("nuria1234");
	    driver.findElement(By.cssSelector(".btn-primary")).click();
	    driver.findElement(By.linkText("Gestionar Productos")).click();
	    driver.findElement(By.linkText("Buscar Producto")).click();
	    driver.findElement(By.linkText("Ropa")).click();
	    driver.findElement(By.linkText("Detalles")).click();
	  }
	 
	 @Test
	  public void verFavoritos() {
			driver.get("http://localhost:" + port + "/login");
		    driver.manage().window().setSize(new Dimension(1174, 1118));
		    driver.findElement(By.id("username")).sendKeys("nuria.sane.r@gmail.com");
			driver.findElement(By.id("password")).sendKeys("nuria1234");
		    driver.findElement(By.cssSelector(".btn-primary")).click();
	    driver.findElement(By.linkText("Gestionar Productos")).click();
	    driver.findElement(By.linkText("Mis productos favoritos")).click();
	  }
	
	  @Test
	  public void aadirFavoritoYBorrarFavortio() {
			driver.get("http://localhost:" + port + "/login");
		    driver.manage().window().setSize(new Dimension(1174, 1118));
		    driver.findElement(By.id("username")).sendKeys("nuria.sane.r@gmail.com");
			driver.findElement(By.id("password")).sendKeys("nuria1234");
		    driver.findElement(By.cssSelector(".btn-primary")).click();
	    driver.findElement(By.cssSelector(".horizontal-list:nth-child(2) > .product-container:nth-child(3) .add-to-favorites")).click();
	    driver.findElement(By.linkText("Gestionar Productos")).click();
	    driver.findElement(By.linkText("Mis productos favoritos")).click();
	    driver.findElement(By.cssSelector(".product-list:nth-child(8) .glyphicon")).click();
	  }
	  
	  @Test
	  public void aadirReseaProductoYEliminarResea() {
			driver.get("http://localhost:" + port + "/login");
		    driver.manage().window().setSize(new Dimension(1174, 1118));
		    driver.findElement(By.id("username")).sendKeys("nuria.sane.r@gmail.com");
			driver.findElement(By.id("password")).sendKeys("nuria1234");
		    driver.findElement(By.cssSelector(".btn-primary")).click();
	    driver.findElement(By.linkText("Gestionar Productos")).click();
	    driver.findElement(By.linkText("Productos Comprados")).click();
	    driver.findElement(By.cssSelector(".product-container:nth-child(2) .view-detail-button")).click();
	    driver.findElement(By.cssSelector(".star:nth-child(2) > .fa")).click();
	    driver.findElement(By.id("comment")).click();
	    driver.findElement(By.id("comment")).sendKeys("No me ha gustado el olor");
	    driver.findElement(By.cssSelector(".btn-primary")).click();
	    driver.findElement(By.cssSelector(".review-list:nth-child(4) .delete-review")).click();
	  }
	
	
	  @Test
	  public void borrarProducto() {
			driver.get("http://localhost:" + port + "/login");
		    driver.manage().window().setSize(new Dimension(1174, 1118));
		    driver.findElement(By.id("username")).sendKeys("nuria.sane.r@gmail.com");
			driver.findElement(By.id("password")).sendKeys("nuria1234");
		    driver.findElement(By.cssSelector(".btn-primary")).click();
	    driver.findElement(By.linkText("Gestionar Productos")).click();
	    driver.findElement(By.linkText("Ver mis productos")).click();
	    {
	      WebElement element = driver.findElement(By.cssSelector(".horizontal-list"));
	      Actions builder = new Actions(driver);
	      builder.moveToElement(element).clickAndHold().perform();
	    }
	    {
	      WebElement element = driver.findElement(By.cssSelector(".horizontal-list"));
	      Actions builder = new Actions(driver);
	      builder.moveToElement(element).perform();
	    }
	    {
	      WebElement element = driver.findElement(By.cssSelector(".horizontal-list"));
	      Actions builder = new Actions(driver);
	      builder.moveToElement(element).release().perform();
	    }
	    driver.findElement(By.cssSelector(".product-container:nth-child(13) .glyphicon")).click();
	  }
	  
	  @Test
	  public void carritoVacioVer() {
		  driver.get("http://localhost:" + port + "/login");
		    driver.manage().window().setSize(new Dimension(1174, 1118));
		    driver.findElement(By.id("username")).sendKeys("nuria.sane.r@gmail.com");
			driver.findElement(By.id("password")).sendKeys("nuria1234");
		    driver.findElement(By.cssSelector(".btn-primary")).click();
	    driver.findElement(By.cssSelector("a > .glyphicon-shopping-cart")).click();
	  }
	  
	  @Test
	  public void compraSinStock() {
		  driver.get("http://localhost:" + port + "/login");
		    driver.manage().window().setSize(new Dimension(1174, 1118));
		    driver.findElement(By.id("username")).sendKeys("nuria.sane.r@gmail.com");
			driver.findElement(By.id("password")).sendKeys("nuria1234");
		    driver.findElement(By.cssSelector(".btn-primary")).click();
	    driver.findElement(By.cssSelector(".product-container:nth-child(2) > .add-to-cart-section:nth-child(5) .add-to-cart")).click();
	    {
	      WebElement element = driver.findElement(By.cssSelector(".horizontal-list"));
	      Actions builder = new Actions(driver);
	      builder.moveToElement(element).clickAndHold().perform();
	    }
	    {
	      WebElement element = driver.findElement(By.cssSelector(".horizontal-list"));
	      Actions builder = new Actions(driver);
	      builder.moveToElement(element).perform();
	    }
	    {
	      WebElement element = driver.findElement(By.cssSelector(".horizontal-list"));
	      Actions builder = new Actions(driver);
	      builder.moveToElement(element).release().perform();
	    }
	    driver.findElement(By.cssSelector(".add-to-cart-section:nth-child(4) .add-to-cart")).click();
	    {
	      WebElement element = driver.findElement(By.cssSelector(".horizontal-list"));
	      Actions builder = new Actions(driver);
	      builder.moveToElement(element).clickAndHold().perform();
	    }
	    {
	      WebElement element = driver.findElement(By.cssSelector(".horizontal-list"));
	      Actions builder = new Actions(driver);
	      builder.moveToElement(element).perform();
	    }
	    {
	      WebElement element = driver.findElement(By.cssSelector(".horizontal-list"));
	      Actions builder = new Actions(driver);
	      builder.moveToElement(element).release().perform();
	    }
	  }
	  
	  @Test
	  public void comprarUnProducto() {
		  driver.get("http://localhost:" + port + "/login");
		    driver.manage().window().setSize(new Dimension(1174, 1118));
		    driver.findElement(By.id("username")).sendKeys("nuria.sane.r@gmail.com");
			driver.findElement(By.id("password")).sendKeys("nuria1234");
		    driver.findElement(By.cssSelector(".btn-primary")).click();
	    driver.findElement(By.linkText("Detalles")).click();
	    driver.findElement(By.cssSelector("form:nth-child(2) .glyphicon")).click();
	    driver.findElement(By.cssSelector(".pay-button")).click();
	  }
	  
//	  public String waitForWindow(int timeout) {
//		    try {
//		      Thread.sleep(timeout);
//		    } catch (InterruptedException e) {
//		      e.printStackTrace();
//		    }
//		    Set<String> whNow = driver.getWindowHandles();
//		    Set<String> whThen = (Set<String>) vars.get("window_handles");
//		    if (whNow.size() > whThen.size()) {
//		      whNow.removeAll(whThen);
//		    }
//		    return whNow.iterator().next();
//		  }
//		  @Test
//		  public void recargaPaypalBien() {
//			  driver.get("http://localhost:" + port + "/login");
//			    driver.manage().window().setSize(new Dimension(1174, 1118));
//			    driver.findElement(By.id("username")).sendKeys("nuria.sane.r@gmail.com");
//				driver.findElement(By.id("password")).sendKeys("nuria1234");
//			    driver.findElement(By.cssSelector(".btn-primary")).click();
//		    driver.findElement(By.linkText("Usuario")).click();
//		    driver.findElement(By.cssSelector(".show > li:nth-child(1) > a")).click();
//		    driver.findElement(By.id("amount")).sendKeys("10");
//		    driver.switchTo().frame(0);
//		    vars.put("window_handles", driver.getWindowHandles());
//		    driver.findElement(By.cssSelector(".paypal-button-number-0 > .paypal-button-label-container")).click();
//		    vars.put("win4254", waitForWindow(2000));
//		    vars.put("root", driver.getWindowHandle());
//		    driver.switchTo().window(vars.get("win4254").toString());
//		    driver.findElement(By.id("email")).click();
//		    driver.findElement(By.id("email")).sendKeys("sb-rbh47g29898417@personal.example.com");
//		    driver.findElement(By.id("password")).sendKeys("R_h.D>5t");
//		    driver.findElement(By.id("btnNext")).click();
//		    driver.findElement(By.id("btnLogin")).click();
//		    driver.findElement(By.id("payment-submit-btn")).click();
//		    {
//		      WebElement element = driver.findElement(By.id("payment-submit-btn"));
//		      Actions builder = new Actions(driver);
//		      builder.moveToElement(element).perform();
//		    }
//		    {
//		      WebElement element = driver.findElement(By.tagName("body"));
//		      Actions builder = new Actions(driver);
//		      builder.moveToElement(element, 0, 0).perform();
//		    }
//		    driver.close();
//		    driver.switchTo().window(vars.get("root").toString());
//		  }
		  
//		  @Test
//		  public void paypalError() {
//			  driver.get("http://localhost:" + port + "/login");
//			    driver.manage().window().setSize(new Dimension(1174, 1118));
//			    driver.findElement(By.id("username")).sendKeys("nuria.sane.r@gmail.com");
//				driver.findElement(By.id("password")).sendKeys("nuria1234");
//			    driver.findElement(By.cssSelector(".btn-primary")).click();
//		    driver.findElement(By.linkText("Usuario")).click();
//		    driver.findElement(By.cssSelector(".show > li:nth-child(1) > a")).click();
//		    driver.findElement(By.id("amount")).click();
//		    driver.findElement(By.id("amount")).sendKeys("-2");
//		    driver.switchTo().frame(0);
//		    vars.put("window_handles", driver.getWindowHandles());
//		    driver.findElement(By.cssSelector(".paypal-button-number-0:nth-child(1)")).click();
//		    vars.put("win6774", waitForWindow(2000));
//		    vars.put("root", driver.getWindowHandle());
//		    driver.switchTo().window(vars.get("win6774").toString());
//		    driver.close();
//		    driver.switchTo().window(vars.get("root").toString());
//		  }
}
