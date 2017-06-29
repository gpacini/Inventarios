package com.serinse.web.controllers.inventory;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import com.serinse.common.Constants;
import com.serinse.common.helpers.FileUtilities;
import com.serinse.ejb.impl.general.PhotoBean;
import com.serinse.pers.entity.general.Photo;
import com.serinse.pers.entity.inventory.Product;
import com.serinse.pers.exception.FailProcessException;

@Named("photoUploadController")
@ViewScoped
public class PhotoUploadController implements Serializable {


	private static final long serialVersionUID = 3200414608381502795L;

	@Inject
	PhotoBean photoBean;
	
	@Inject
	ProductsController productsController;
	
	private String serverFilePath;
	private Product productToLink;
	
	public void uploadFile(FileUploadEvent event){
		serverFilePath = Constants.IMAGES_LOCATION;
		productToLink = productsController.getProductToEdit();
		if( productToLink == null ) return;
		try {
			UploadedFile uploadedFile = event.getFile();
			String name = uploadedFile.getFileName();
			InputStream stream = uploadedFile.getInputstream();
			Long newDate = (new Date()).getTime();
			String completeFilename = newDate + name;
			FileUtilities.createDirectoryAndSaveFile(serverFilePath + "products/" , stream, completeFilename);
			
			Photo photo = photoBean.findByTableAndId(Constants.products_photos_table, productToLink.getId());
			if( photo == null ){
				photo = new Photo();
			} else {
				try{
					FileUtilities.deleteFile(serverFilePath + "products/" + photo.getName() ); 
				} catch(Exception e){
					System.out.println("No se pudo eliminar la foto");
				}
			}
			photo.setDirectory("products");
			photo.setName(completeFilename);
			photo.setTable(Constants.products_photos_table);
			
			productToLink.setPhoto(photo);
			
		} catch (IOException | FailProcessException | NullPointerException e) {
			FacesContext.getCurrentInstance().addMessage(null, 
					new FacesMessage(FacesMessage.SEVERITY_FATAL, "No se pudo subir el archivo", ""));
			e.printStackTrace();
		}
	}

	public Product getProductToLink() {
		return productToLink;
	}

	public void setProductToLink(Product productToLink) {
		this.productToLink = productToLink;
	}

}
