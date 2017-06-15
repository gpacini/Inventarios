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

import org.primefaces.context.RequestContext;
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
	
	private String serverFilePath;
	private Product productToLink;
	
	private String filesPath;
	
	public void uploadFile(FileUploadEvent event){
		serverFilePath = Constants.IMAGES_LOCATION;
		if( productToLink == null ) return;
		try {
			UploadedFile uploadedFile = event.getFile();
			String name = uploadedFile.getFileName();
			InputStream stream = uploadedFile.getInputstream();
			Long newDate = (new Date()).getTime();
			String completeFilename = newDate + name;
			FileUtilities.createDirectoryAndSaveFile(serverFilePath + "products/" , stream, completeFilename);
			
			Photo photo = new Photo();
			photo.setDirectory("products");
			photo.setName(completeFilename);
			photo.setFkId(productToLink.getId());
			photo.setTable("products");
			photo.setDescription(productToLink.getMaterial());
			photo.setTitle(productToLink.getCode());
			
			photoBean.save(photo);
			
			productToLink.setPhoto(photo);
			
			RequestContext.getCurrentInstance().execute("PF('uploadPhotoDialog').hide();");
			
		} catch (IOException | FailProcessException | NullPointerException e) {
			FacesContext.getCurrentInstance().addMessage(null, 
					new FacesMessage(FacesMessage.SEVERITY_FATAL, "No se pudo subir el archivo", ""));
			e.printStackTrace();
		}
	}

	public String getFilesPath() {
		return filesPath;
	}

	public void setFilesPath(String filesPath) {
		this.filesPath = filesPath;
	}

	public Product getProductToLink() {
		return productToLink;
	}

	public void setProductToLink(Product productToLink) {
		this.productToLink = productToLink;
	}

}
