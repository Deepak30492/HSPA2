package com.uhi.hsp.service;

import com.dhp.sdk.beans.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.uhi.hsp.dto.EuaRequestBody;
import com.uhi.hsp.dto.HspRequestBody;
import com.uhi.hsp.model.Address;
import com.uhi.hsp.model.Categories;
import com.uhi.hsp.model.Customer;
import com.uhi.hsp.model.Fulfillments;
import com.uhi.hsp.model.Practitioner;
import com.uhi.hsp.model.Provider;
import com.uhi.hsp.repository.AddressRepository;
import com.uhi.hsp.repository.BillingRepository;
import com.uhi.hsp.repository.CustomerRepository;
import com.uhi.hsp.repository.FulfillmentsRepository;
import com.uhi.hsp.repository.PractitionerRepository;
import com.uhi.hsp.repository.ProviderRepository;

import org.modelmapper.ModelMapper;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class StatusService {
	RestTemplate restTemplate = new RestTemplate();
	private final ProviderRepository _providerRepo;

	@Value("classpath:static/on_search.json")
	private Resource on_searchFile;

	@Value("classpath:static/on_select.json")
	private Resource on_selectFile;

	@Value("classpath:static/on_init.json")
	private Resource on_initFile;

	@Value("classpath:static/on_confirm.json")
	private Resource on_confirmFile;

	@Value("classpath:static/on_status.json")
	private Resource on_statusFile;

	final ObjectMapper mapper;

	final ModelMapper modelMapper;
	@Autowired
	private FulfillmentsRepository fulfillmentsRepo;

	@Autowired
	private BillingRepository billingRepository;
	@Autowired
	private AddressRepository addressRepository;
	@Autowired
	private PractitionerRepository practitionerRepo;
	// @Autowired
	private OnTBody mapToJson = new OnTBody();
	@Autowired
	CustomerRepository customerRepo;
	
	

	public StatusService(ObjectMapper mapper, ProviderRepository providerRepo, ModelMapper modelMapper) {
		this.mapper = mapper;
		this._providerRepo = providerRepo;
		this.modelMapper = modelMapper;
	}

	public EuaRequestBody mapSearch(HspRequestBody req) throws IOException {
		EuaRequestBody searchedProviderData = getSearchByProviderName(req);
		String messageId = req.getContext().getMessage_id();
		HttpEntity<Object> entity = generateEntityWithHeaders(searchedProviderData, messageId);
		// String endpoint = req.getContext().getConsumer_uri();
		// restTemplate.postForObject(endpoint + "/on_search", entity, String.class);

		return searchedProviderData;
	}

	public OnTBody mapSelect(HspRequestBody req) throws IOException {
		OnTBody selectedProvider = selectProviderById(req);
		String messageId = req.getContext().getMessage_id();
		HttpEntity<Object> entity = generateEntityWithHeaders(selectedProvider, messageId);
		// String endpoint = req.getContext().getConsumer_uri();
		// restTemplate.postForObject(endpoint + "/on_select", entity, String.class);
		return selectedProvider;
	}

	public OnTBody mapInit(HspRequestBody req) throws IOException {
		OnTBody initData = initRequest(req);
		String messageId = req.getContext().getMessage_id();
		HttpEntity<Object> entity = generateEntityWithHeaders(initData, messageId);
		// String endpoint = req.getContext().getConsumer_uri();
		// restTemplate.postForObject(endpoint + "/on_init", entity, String.class);
		return initData;

	}

	public OnTBody mapConfirm(HspRequestBody req) throws IOException {
		OnTBody initData = confirmRequest(req);// change kr
		String messageId = req.getContext().getMessage_id();
		HttpEntity<Object> entity = generateEntityWithHeaders(initData, messageId);
		// String endpoint = req.getContext().getConsumer_uri();
		// restTemplate.postForObject(endpoint + "/on_init", entity, String.class);
		return initData;

	}

	public OnTBody mapStatus(HspRequestBody req) throws IOException {
		String messageId = req.getContext().getMessage_id();
		OnTBody body = mapper.readValue(on_statusFile.getFile(), OnTBody.class);
		body.getContext().setMessage_id(messageId);
		return body;

	}

	private EuaRequestBody getSearchByProviderName(HspRequestBody req) {
		EuaRequestBody euaRequestBody = new EuaRequestBody();
		euaRequestBody = (EuaRequestBody) extracteContext(req, euaRequestBody);
		System.out.println("__________" + euaRequestBody);
		Provider provider = new Provider();
		com.dhp.sdk.beans.Provider providerDto;
		provider.setName(req.getMessage().getIntent().getProvider().getDescriptor().getName());
		Example<Provider> providerExample = Example.of(provider);
		List<Provider> result = _providerRepo.findAll(providerExample);
		// System.out.println("_____________"+result.get(0));

		if (!result.isEmpty()) {
			providerDto = modelMapper.map(result.get(0), com.dhp.sdk.beans.Provider.class);
			providerDto = extractedProviderDescriptor(providerDto, result);
			System.out.println("_____________" + providerDto);
			int counterForPerson = 0;
			providerDto = extractedCategoryDescriptorNames(providerDto, result);
			providerDto = extractedPerson(providerDto, result, counterForPerson);
			List<Fulfillments> fulfillmentInResult = result.get(0).getFulfillments();
			int counterForTimer = 0;
			ArrayList<com.dhp.sdk.beans.Provider> providerList = new ArrayList<>();
			providerList.add(providerDto);
			euaRequestBody = extractedContext(euaRequestBody, providerList);
		}
		return euaRequestBody;
	}

	// selectProviderById (Start)

	private OnTBody selectProviderById(HspRequestBody req) {
		Integer providerId = Integer.parseInt(req.getMessage().getOrder().getProvider().getProviderId());
		Integer fulfillmentId = Integer.parseInt(req.getMessage().getOrder().getItems().get(0).getFulfillment_id());
		Fulfillments fulfillmentData = fulfillmentsRepo.findByFulfillmentIdAndProviderProviderId(fulfillmentId,
				providerId);
		mapToJson = this.mapToJson(req, fulfillmentData);
		return mapToJson;

	}

	// Init start
	private OnTBody initRequest(HspRequestBody req) {
		Integer providerId = Integer.parseInt(req.getMessage().getOrder().getProvider().getProviderId());
		Integer fulfillmentId = Integer.parseInt(req.getMessage().getOrder().getItems().get(0).getFulfillment_id());
		Fulfillments fulfillmentData = fulfillmentsRepo.findByFulfillmentIdAndProviderProviderId(fulfillmentId,
				providerId);
		System.out.println("++++" + fulfillmentData);
		System.out.println(providerId + " " + fulfillmentId);
		mapToJson = this.mapToJson(req, fulfillmentData);
		return mapToJson;
	}

	// init end

	// confirm
	private OnTBody confirmRequest(HspRequestBody req) {
		Integer providerID = Integer.parseInt(req.getMessage().getOrder().getProvider().getProviderId());
		System.out.println("" + providerID);
		OnTBody requestBody = new OnTBody();
		requestBody = (OnTBody) extracteContext(req, requestBody);
		// mapToJson = this.mapToJson(req, fulfillmentData);
		return requestBody;
	}

	// end

	private com.dhp.sdk.beans.Provider extractedProviderDescriptor(com.dhp.sdk.beans.Provider providerDto,
			List<Provider> result) {
		Descriptor providerDescriptor = new Descriptor();
		providerDescriptor.setName(result.get(0).getName());
		providerDto.setDescriptor(providerDescriptor);

		return providerDto;
	}

	private com.dhp.sdk.beans.Provider extractedCategoryDescriptorNames(com.dhp.sdk.beans.Provider providerDto,
			List<Provider> result) {
		int categoryDescriptorCount = 0;
		for (Categories categories : result.get(0).getCategories()) {
			Descriptor categoryDescriptor = new Descriptor();
			categoryDescriptor.setName(categories.getName());
			providerDto.getCategories().get(categoryDescriptorCount).setDescriptor(categoryDescriptor);
			categoryDescriptorCount++;
		}
		return providerDto;
	}

	private EuaRequestBody extractedContext(EuaRequestBody euaRequestBody,
			ArrayList<com.dhp.sdk.beans.Provider> providerList) {
		Message message = new Message();
		Catalog catalog = new Catalog();
		Descriptor catlogDescriptor = new Descriptor();
		catlogDescriptor.setName("Practo");
		catalog.setDescriptor(catlogDescriptor);
		catalog.setProviders(providerList);

		message.setCatalog(catalog);
		euaRequestBody.setMessage(message);
		return euaRequestBody;
	}

	
	
	private com.dhp.sdk.beans.Provider extractedTimer(com.dhp.sdk.beans.Provider providerDto,
			List<Fulfillments> fulfillmentInResult, int counterForTimer) {
		for (Fulfillments fulfillments : fulfillmentInResult) {
			Start start = new Start();
			Time time = new Time();
			time.setTimestamp(fulfillments.getStartTime());
			start.setTime(time);

			End end = new End();
			time = null;
			time = new Time();
			time.setTimestamp(fulfillments.getEndTime());
			end.setTime(time);

			providerDto.getFulfillments().get(counterForTimer).setStart(start);
			providerDto.getFulfillments().get(counterForTimer).setEnd(end);
			counterForTimer++;
		}

		return providerDto;
	}

	private com.dhp.sdk.beans.Provider extractedPerson(com.dhp.sdk.beans.Provider providerDto, List<Provider> result,
			int counterForPerson) {
		for (Fulfillments fulfillments : result.get(0).getFulfillments()) {
			Person personDto = new Person();
			personDto.setPersonId(String.valueOf(fulfillments.getPractitionerId().getPractitionerId()));
			personDto.setName(fulfillments.getPractitionerId().getName());
			personDto.setCred(fulfillments.getPractitionerId().getCred());
			if (fulfillments.getPractitionerId().getGender() == 'M') {
				personDto.setGender("Male");
			} else if (fulfillments.getPractitionerId().getGender() == 'F') {
				personDto.setGender("Female");
			} else {
				personDto.setGender("Others");
			}
			personDto.setImage(fulfillments.getPractitionerId().getImage());

			providerDto.getFulfillments().get(counterForPerson).setPerson(personDto);
			counterForPerson++;

		}

		return providerDto;
	}

	private Object extracteContext(HspRequestBody req, Object body) {
		if (body instanceof OnTBody) {
			OnTBody onTbody = (OnTBody) body;
			onTbody.setContext(req.getContext());
			if (req.getContext().getAction().equalsIgnoreCase("select")) {
				onTbody.getContext().setAction("on_select");
			}

			if (req.getContext().getAction().equalsIgnoreCase("init")) {
				onTbody.getContext().setAction("on_init");
			}

			if (req.getContext().getAction().equalsIgnoreCase("confirm")) {
				onTbody.getContext().setAction("on_confirm");
			}

			body = onTbody;
		}

		if (body instanceof EuaRequestBody) {
			EuaRequestBody euaBody = (EuaRequestBody) body;
			euaBody.setContext(req.getContext());
			euaBody.getContext().setAction("on_search");
			body = euaBody;
		}

		return body;
	}

	private HttpEntity<Object> generateEntityWithHeaders(Object body, String messageId) throws IOException {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("signature", "a5UsBIOk1dORJAUQu2UPSWXKjHq1l4TdbCawhtdbtV0=");
		headers.set("created", "25/02/2022 15:53:12");
		headers.set("expires", "26/02/2032 03:53:12");
		headers.set("keyId", "uhidevendra.southeastasia.cloudapp.azure.com.k1");
		headers.set("subscriber_id", "uhidevendra.southeastasia.cloudapp.azure.com..LREG");
		headers.set("pub_key_id", "tTcag3f4aSFf7jEF9I7cENgm7zNJN59YzpJRIQeWSKo=");
		return new HttpEntity<>(body, headers);
	}

	public OnTBody mapToJson(HspRequestBody req, Fulfillments fulfillmentData) {
		String action = req.getContext().getAction();
		// System.out.println(action);
		Fulfillment fulfillmentDto;
		OnTBody requestBody = new OnTBody();

		requestBody = (OnTBody) extracteContext(req, requestBody);
		Practitioner practitionerData = fulfillmentData.getPractitionerId();
		if (action.equalsIgnoreCase("select")) {
			practitionerData.setStatus("Prvisonaly-Booked");
			practitionerData.setStartTime(fulfillmentData.getStartTime());
			practitionerData.setEndTime(fulfillmentData.getEndTime());
			practitionerRepo.save(practitionerData);
		}
		double sgst = practitionerData.getSgst();
		double cgst = practitionerData.getCgst();
		double phrHandlingFees = practitionerData.getPhrHandlingFees();
		String consultationCharge = practitionerData.getConsultationCharge();
		Double consultationValue = Double.parseDouble(consultationCharge);
		Double value = sgst + cgst + phrHandlingFees + consultationValue;
		Person personData = new Person();
		System.out.println(fulfillmentData);
		if (fulfillmentData != null) {
			fulfillmentDto = modelMapper.map(fulfillmentData, com.dhp.sdk.beans.Fulfillment.class);

			personData = modelMapper.map(practitionerData, Person.class);

			personData.setPersonId(practitionerData.getPractitionerId().toString());

			fulfillmentDto.setPerson(personData);

			Range range = new Range();
			range.setStart(fulfillmentData.getStartTime());
			range.setEnd(fulfillmentData.getEndTime());

			Time time = new Time();
			time.setRange(range);
			fulfillmentDto.setTime(time);
			Provider provider = fulfillmentData.getProvider();
			com.dhp.sdk.beans.Provider providerData = new com.dhp.sdk.beans.Provider();
			providerData.setProviderId(provider.getProviderId().toString());
			Descriptor descriptor = new Descriptor();
			descriptor.setName(fulfillmentDto.getType());
			providerData.setDescriptor(descriptor);

			Items item = new Items();
			item.setItemId(req.getMessage().getOrder().getItems().get(0).getItemId());
			item.setFulfillment_id(req.getMessage().getOrder().getItems().get(0).getFulfillment_id());
			item.setProvider_id(fulfillmentData.getProvider().getProviderId().toString());
			item.setDescriptor(descriptor);
			ArrayList<com.dhp.sdk.beans.Items> itemList = new ArrayList();
			itemList.add(item);

			Price price = new Price();
			ObjectMapper mapper = new ObjectMapper();
			JsonNode node1, node2, node3, node4;
			String input1 = "{\"./dhp-0_7_1.consultation\": \"" + consultationCharge + "\"}";
			String input2 = "{\"./dhp-0_7_1.phr_handling_fees\": \"" + sgst + "\"}";
			String input3 = "{\"./ind-gstin.cgst\": \"" + cgst + "\"}";
			String input4 = "{\"./ind-gstin.sgst\": \"" + phrHandlingFees + "\"}";
			try {
				node1 = mapper.readTree(input1);
				node2 = mapper.readTree(input2);
				node3 = mapper.readTree(input3);
				node4 = mapper.readTree(input4);
				List<JsonNode> nodeList = new ArrayList<JsonNode>();
				nodeList.add(node1);
				nodeList.add(node2);
				nodeList.add(node3);
				nodeList.add(node4);
				price.setBreakup(nodeList);

			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			price.setCurrency(practitionerData.getCurrency());
			price.setValue(value.toString());

			Quote quote = new Quote();
			quote.setPrice(price);

			OnOrder order = new OnOrder();
			order.setFulfillment(fulfillmentDto);
			order.setItems(itemList);
			order.setProvider(providerData);
			order.setQuote(quote);

			// action = action.replace("\"", "");
			if (action.equalsIgnoreCase("init")) {
				// billingRepository.save(billing2);
				JsonNode customer = req.getMessage().getOrder().getFulfillment().getCustomer();
				fulfillmentDto.setCustomer(customer);
				System.out.println("____________________________________");
				Billing billing = req.getMessage().getOrder().getBilling();
				JsonNode customerNode = req.getMessage().getOrder().getFulfillment().getCustomer();
				
				String customerId =  customerNode.get("id").textValue();
				String customerCred =  customerNode.get("cred").textValue();
				com.uhi.hsp.model.Customer customerData = new Customer();
				customerData.setCred(customerCred);
				customerData.setCustomerId(customerId);
				customerRepo.save(customerData);
				com.uhi.hsp.model.Billing billingData = modelMapper.map(billing, com.uhi.hsp.model.Billing.class);
				/*Address add = new Address();
				Address address = billingData.getAddress();
				add = modelMapper.map(address, com.uhi.hsp.model.Address.class);
				billingData.setAddress(add);*/
				Fulfillments fData;
				fData = modelMapper.map(fulfillmentData, com.uhi.hsp.model.Fulfillments.class);
				fData.setStatus("Booked");
				billingData.setFulfillments(fData);
				billingData.setCustomer(customerData);
				System.out.println(billingData);
				billingRepository.save(billingData);
				billing = modelMapper.map(billing, com.dhp.sdk.beans.Billing.class);
				order.setBilling(billing);
				order.setId("1");// ask
				order.setState("Booked");// ask
			}
			
			OnMessage message = new OnMessage();
			message.setOrder(order);
			requestBody.setMessage(message);

		}
		
		
		
		
		
		return requestBody;

	}

}
