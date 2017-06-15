package com.serinse.web.general;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.commons.io.IOUtils;
import org.jboss.resteasy.plugins.providers.multipart.InputPart;
import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;

import com.serinse.common.Constants;
import com.serinse.common.FileHelpers;
import com.serinse.ejb.impl.general.PhotoBean;
import com.serinse.pers.entity.general.Photo;
import com.serinse.web.common.response.BaseResponse;
import com.serinse.web.common.response.JSONResponse;
import com.serinse.web.users.helpers.ObjectGetResponse;

@Path("photos")
public class PhotoService {

	@Inject
	PhotoBean photoBean;

	@GET
	@Path("get/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONResponse get(@PathParam("id") Long id){
		Photo p = photoBean.findById(id);
		if( p == null ){
			return new BaseResponse(false, "No existe la foto");
		}
		return new ObjectGetResponse<Photo>(p, "Foto encontrada");
	}
	
	@POST
	@Path("upload")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONResponse upload(MultipartFormDataInput input) {

		String path = Constants.IMAGES_LOCATION;
		String fileName = "";
		String directory, table, name, title, description;
		Long fkId;

		Map<String, List<InputPart>> uploadForm = input.getFormDataMap();
		try {
			List<InputPart> inputParts = uploadForm.get("directory");
			directory = inputParts.get(0).getBodyAsString();

			inputParts = uploadForm.get("table");
			table = inputParts.get(0).getBodyAsString();

			inputParts = uploadForm.get("title");
			title = inputParts.get(0).getBodyAsString();

			inputParts = uploadForm.get("description");
			description = inputParts.get(0).getBodyAsString();

			inputParts = uploadForm.get("fkId");
			String tmp = inputParts.get(0).getBodyAsString();
			fkId = Long.parseLong(tmp);

			inputParts = uploadForm.get("photo");

			for (InputPart inputPart : inputParts) {

				MultivaluedMap<String, String> header = inputPart.getHeaders();
				fileName = FileHelpers.getFileName(header);
				fileName = fileName.toLowerCase();

				if (!fileName.contains(".png") && !fileName.contains(".jpg") && !fileName.contains(".jpeg")) {
					return new BaseResponse(false, "El archivo no es una foto valida");
				}

				// convert the uploaded file to inputstream
				InputStream inputStream = inputPart.getBody(InputStream.class, null);

				byte[] bytes = IOUtils.toByteArray(inputStream);

				// constructs upload file path
				name = FileHelpers.hashName(System.currentTimeMillis() + fileName);
				fileName = path + directory + Constants.FILES_SEPARATOR + name;
				System.out.println("Archivo: " + fileName);
				FileHelpers.writeFile(bytes, fileName);

				Photo photo = new Photo();
				photo.setDescription(description);
				photo.setDirectory(directory);
				photo.setFkId(fkId);
				photo.setName(name);
				photo.setTable(table);
				photo.setTitle(title);
				photoBean.save(photo);

				return new ObjectGetResponse<Photo>(photo, "Se guardo la foto");
			}

		} catch (IOException e) {
			e.printStackTrace();
			return new BaseResponse(false, "No se pudo guardar la foto.");
		}

		return new BaseResponse(false, "No se pudo guardar la foto.");
	}
}
