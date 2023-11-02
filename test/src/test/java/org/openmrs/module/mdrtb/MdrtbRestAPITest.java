package org.openmrs.module.mdrtb;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class MdrtbRestAPITest {
	
	public static String UUID_REGEX;
	
	public static String USERNAME;
	
	public static String PASSWORD;
	
	static {
		RestAssured.baseURI = "http://localhost:8080/openmrs/ws/rest/v1"; // OpenMRS server must be running
		USERNAME = "admin";
		PASSWORD = "Admin1234";
		UUID_REGEX = "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}";
	}
	
	/*	@Test
		public void testTemp() {
			String[] uuids = { "31b27f6a-0370-102d-b0e3-001ec94a0cc1","31b2803c-0370-102d-b0e3-001ec94a0cc1","5f1c76c0-28b6-4e95-bbcf-17afbb933c5d","ff22f976-af7d-4198-830a-a93e473cb4b1","31c64f86-0370-102d-b0e3-001ec94a0cc1","31c64dd8-0370-102d-b0e3-001ec94a0cc1","31c64eb4-0370-102d-b0e3-001ec94a0cc1","31c7bd8a-0370-102d-b0e3-001ec94a0cc1","31c64c34-0370-102d-b0e3-001ec94a0cc1","31bef3d0-0370-102d-b0e3-001ec94a0cc1","31afed04-0370-102d-b0e3-001ec94a0cc1","31b09a60-0370-102d-b0e3-001ec94a0cc1","31b4a3ee-0370-102d-b0e3-001ec94a0cc1","31b4a8e4-0370-102d-b0e3-001ec94a0cc1","31b49fca-0370-102d-b0e3-001ec94a0cc1","31b4a4c0-0370-102d-b0e3-001ec94a0cc1","31b4a592-0370-102d-b0e3-001ec94a0cc1","31b4a664-0370-102d-b0e3-001ec94a0cc1","31b4a740-0370-102d-b0e3-001ec94a0cc1","31b4a812-0370-102d-b0e3-001ec94a0cc1","31b4a9b6-0370-102d-b0e3-001ec94a0cc1","31b4aa88-0370-102d-b0e3-001ec94a0cc1","31b64c6c-0370-102d-b0e3-001ec94a0cc1","31bb3c54-0370-102d-b0e3-001ec94a0cc1","31bb3e02-0370-102d-b0e3-001ec94a0cc1","31bb3ed4-0370-102d-b0e3-001ec94a0cc1","18876a12-3a06-450c-af3c-aaa774e902b6","b877ac5a-7e03-4c38-bab5-6afe5ac76a74","31b0841c-0370-102d-b0e3-001ec94a0cc1","31b08840-0370-102d-b0e3-001ec94a0cc1","31b0907e-0370-102d-b0e3-001ec94a0cc1","31b0998e-0370-102d-b0e3-001ec94a0cc1","31b1e398-0370-102d-b0e3-001ec94a0cc1","31ae749c-0370-102d-b0e3-001ec94a0cc1","31b64b9a-0370-102d-b0e3-001ec94a0cc1","a60a046e-0739-4bd8-83b6-fe86e4771b48","e1236682-8451-4c75-b4ba-44457d1ebf43","b4d9a4cc-a6cd-431f-8915-5dd96e7cf678","31bef15a-0370-102d-b0e3-001ec94a0cc1","31ce35fc-0370-102d-b0e3-001ec94a0cc1","31b41c26-0370-102d-b0e3-001ec94a0cc1","31ce2dbe-0370-102d-b0e3-001ec94a0cc1","31ce2f12-0370-102d-b0e3-001ec94a0cc1","31ce3052-0370-102d-b0e3-001ec94a0cc1","31bf136a-0370-102d-b0e3-001ec94a0cc1","31bef4ac-0370-102d-b0e3-001ec94a0cc1","31bf10e0-0370-102d-b0e3-001ec94a0cc1","31bf0d84-0370-102d-b0e3-001ec94a0cc1","31bf0f32-0370-102d-b0e3-001ec94a0cc1","31bef7f4-0370-102d-b0e3-001ec94a0cc1","31bf09b0-0370-102d-b0e3-001ec94a0cc1","31bf023a-0370-102d-b0e3-001ec94a0cc1","31befcf4-0370-102d-b0e3-001ec94a0cc1","31bef722-0370-102d-b0e3-001ec94a0cc1","31bef57e-0370-102d-b0e3-001ec94a0cc1","31b4bce4-0370-102d-b0e3-001ec94a0cc1","31bef650-0370-102d-b0e3-001ec94a0cc1","31bb4a82-0370-102d-b0e3-001ec94a0cc1","31bef9a2-0370-102d-b0e3-001ec94a0cc1","31bf100e-0370-102d-b0e3-001ec94a0cc1","31b24d06-0370-102d-b0e3-001ec94a0cc1","31bf080c-0370-102d-b0e3-001ec94a0cc1","31bf065e-0370-102d-b0e3-001ec94a0cc1","31bf1518-0370-102d-b0e3-001ec94a0cc1","31bf128e-0370-102d-b0e3-001ec94a0cc1","31bf1446-0370-102d-b0e3-001ec94a0cc1","8a91ba44-c926-4bdc-802d-f1defe4f4e0f","31bf073a-0370-102d-b0e3-001ec94a0cc1","31bf0e60-0370-102d-b0e3-001ec94a0cc1","31bf030c-0370-102d-b0e3-001ec94a0cc1","31befc22-0370-102d-b0e3-001ec94a0cc1","31bef8d0-0370-102d-b0e3-001ec94a0cc1","985a9b76-de2d-4182-b095-481700ce7c7c","31bb563a-0370-102d-b0e3-001ec94a0cc1","4a45b687-819f-452c-8b34-33ab6536d017","31b9842c-0370-102d-b0e3-001ec94a0cc1","31bb6666-0370-102d-b0e3-001ec94a0cc1","72d8e563-e141-4a5f-ab2e-2f7e3448bdc5","fbef36fd-8f0d-4bac-8b2d-0c6141c91382","b7a7ebe3-e298-4dd7-b711-8fe659a8ce2f","d7f6fb0b-241a-4ac8-993b-a2942c549955","6a6be4e0-9a56-4376-a8b3-9b6a9ec2d9bf","731bdb67-f216-477f-85c2-8af92d999121","12235c33-e627-4636-8b85-8643fadc622e","b24aeae2-6234-4e5d-95ec-97d03545a425","2710ad98-8f02-40a7-b0d4-8d0c6668dfa9","22c701fb-20e1-43c7-acf3-ea2faa7f7ba3","9ad2fd64-6ffb-4310-b1d8-0c129ae05b0f","73ac78a7-6c49-490a-bb6e-ba94bc6d0479","6b51dc02-1c23-419e-89cd-b9fcfd8f68f2","f98e7ca5-058c-4031-887f-297a660b2f7e","288d86d6-74db-4260-9700-c9cbe8f92d81","a32b1288-3740-4eec-8828-4be4fcc45ce6","d587d8cb-6f41-466d-9426-22c712783cd5","3cbafe53-8630-446e-9905-b83c5c6fd04b","9446085c-86ae-4e06-b571-f8a88217b472","200294f2-8b3e-4fb1-94fa-6755af9ed9c5","eff9438b-f4c9-4b4c-aa82-1e5adc0fe09c","31b4a240-0370-102d-b0e3-001ec94a0cc1","31b4a16e-0370-102d-b0e3-001ec94a0cc1","31b4a312-0370-102d-b0e3-001ec94a0cc1","31b0141e-0370-102d-b0e3-001ec94a0cc1","31aff3c6-0370-102d-b0e3-001ec94a0cc1","31b4a09c-0370-102d-b0e3-001ec94a0cc1","31ccc80c-0370-102d-b0e3-001ec94a0cc1","bfa92f60-d34b-4ae5-acb6-bc7a705ae109","f7b5bf49-cb07-4fca-8c15-93ba92249344","31c2d4be-0370-102d-b0e3-001ec94a0cc1","31c2d3ec-0370-102d-b0e3-001ec94a0cc1","ae16bb6e-3d82-4e14-ab07-2018ee10d311","31c2d590-0370-102d-b0e3-001ec94a0cc1","31c2d31a-0370-102d-b0e3-001ec94a0cc1","31c9eb78-0370-102d-b0e3-001ec94a0cc1","e58da80f-3adf-4a4d-8a7a-43482f9fa5a5","6ea6a201-0afa-4843-b0b6-212121c64f36","31ce3b38-0370-102d-b0e3-001ec94a0cc1","845257f4-642b-4f67-8c57-d82f8982c83c","31b6b8aa-0370-102d-b0e3-001ec94a0cc1","33741f7e-c104-44e5-a1d8-421c7b391ba5","31b5b644-0370-102d-b0e3-001ec94a0cc1","31b4fb78-0370-102d-b0e3-001ec94a0cc1","31c2d73e-0370-102d-b0e3-001ec94a0cc1","31ab3962-0370-102d-b0e3-001ec94a0cc1","d78087db-6146-40d4-9dff-5b249e1b4eca","16364087-fc02-485b-90c4-5e988830b031","9e263164-586f-47a1-824b-a1d205cc51fe","701b646d-a3e0-4556-9fbc-31d88e788464","a1021d1d-338c-48bc-9f0b-b7c4b0f8ee5a","31c2c834-0370-102d-b0e3-001ec94a0cc1","31b6bb34-0370-102d-b0e3-001ec94a0cc1","31b60f40-0370-102d-b0e3-001ec94a0cc1","31b6b7d8-0370-102d-b0e3-001ec94a0cc1","31b0e4ac-0370-102d-b0e3-001ec94a0cc1","31b69906-0370-102d-b0e3-001ec94a0cc1","31b6b986-0370-102d-b0e3-001ec94a0cc1","a690e0c4-3371-49b3-9d52-b390fca3dd90","31c7bbdc-0370-102d-b0e3-001ec94a0cc1","5060d5ce-df8e-4090-b09e-62e40a29201a","31b6002c-0370-102d-b0e3-001ec94a0cc1","31b5fe7e-0370-102d-b0e3-001ec94a0cc1","31b4c61c-0370-102d-b0e3-001ec94a0cc1","31b2955e-0370-102d-b0e3-001ec94a0cc1","31b3d3b0-0370-102d-b0e3-001ec94a0cc1","31c2cfc8-0370-102d-b0e3-001ec94a0cc1","31c94434-0370-102d-b0e3-001ec94a0cc1","31b94ef8-0370-102d-b0e3-001ec94a0cc1","a8f2eacc-2c36-4ddd-b3b8-d80ecfb27bf3","767fed8f-3e64-4567-a2c6-258444296787","3458f801-1532-4055-b7f5-f3adf90ec7c7","d3d3e1a7-7230-4085-af56-690977a479e5","31c095fa-0370-102d-b0e3-001ec94a0cc1","31b474e6-0370-102d-b0e3-001ec94a0cc1","31c6554e-0370-102d-b0e3-001ec94a0cc1","31b4b064-0370-102d-b0e3-001ec94a0cc1","31b2a94a-0370-102d-b0e3-001ec94a0cc1","31c2c5fa-0370-102d-b0e3-001ec94a0cc1","6462d5a2-0a83-4bad-98f8-95b36445dbb0","cb0f30f5-15d3-4eb4-b67e-0416cf940d88","8e6207d7-7ddc-4a2d-bf10-2f134e69e869","4f9b9703-95bf-49a3-b3f4-a10445f64c59","0977d2a9-84a4-40dc-95b3-30d7c709cd92","61d7b45a-9d0f-4e78-a960-609e9a43ada1","9e6bbc35-6d6e-4014-ac41-5152fd22c45f","7f3eda2c-9bcb-425e-bce4-30edd972a5a4","b8135923-db22-4db5-b8c8-7d31a02b4cd3","8abe1e01-f167-4618-9f8e-e21ac3dcdf14","3f5a6930-5ead-4880-80ce-6ab79f4f6cb1","ebde5ed8-4717-472d-9172-599af069e94d","88fbaf49-b1ba-4ca7-b96a-ef632f9b1ffb","066ab5fd-55b1-4c42-84b4-21bb263f6eae","368b276c-908a-421d-b76d-6460ef8d48aa","69abc246-13a9-4cbf-92be-83ac59a8938c","37b2cf33-aa3d-4638-95e1-2f886d7eb06c","c029cb31-867b-4e2e-b75d-e72ee584524a","79dff4a3-4093-4df6-8547-8a85e2569d4c","6d79af1d-ae0c-4e68-8e0f-6cc84c4a9106","97481839-6e94-40af-a98e-1bb01a285e54","31bd79ec-0370-102d-b0e3-001ec94a0cc1","e56514ed-1b3b-4d2e-89f1-564fd6265ebe","5b70ffe6-3ed1-48f5-9793-d397e69780f7","010106ed-ee90-4d81-9b09-d5c274bd8157","59e4cba5-2343-482d-b08e-bb8b84890839","f265c34d-614c-4af4-b279-fe885eafc6f4","9e809189-f7b0-424a-a018-f6dbd5bf1127","e5266c3f-e07e-4502-9e2b-892b00383ff9","645117db-d6be-4dd9-b2d5-21c5fbf9249e","5be32a1d-295c-44d5-9f86-ec28e8e49777","c3bd56cf-ac50-4d10-b6ac-3f4314cf0077","d7c20112-06f3-46dc-8bd5-ac271be894aa","a28051b7-303c-40a8-a09f-137a40b50696","c20ff49c-06f8-4ce6-b5ff-5ec3cdd4a436","4148d306-41df-4c3d-acad-61874fe84594","acb52757-8d76-45c5-97f9-e50cc28b2088","46f5877e-b35a-4329-9842-c8e4d236223b","0464131c-5758-4bcc-9c60-f9bd84aa9738","fef19b50-283b-437d-902f-7d1c5977ee4e","78448ebb-79eb-41e0-a9fa-8f801da2b5ff","3d9d4903-35fe-4dd3-be83-abe7fec6106c","5eec397a-c366-4cf0-8f1d-d355e068d4b6","6f81017a-6454-4077-a830-17b6643a7b62","968b15e0-1c70-4347-af4c-7e8d9b374d90","975233be-8f88-417b-a54c-1dceedea6737","0cdd27ce-c469-466b-9d6e-0f1662ddb6e3","30eece8f-ed94-4241-b89c-2449bede927e","c44661eb-3641-45c1-9fb2-f7ca87adf617","c2358f1f-1d96-497a-849a-9f678b72c657","7531b943-418c-4234-9f95-d18d1fccaa36","dcf5702f-b9f7-4e0c-a502-884c6d5bdc5c","8236b21e-6504-4378-b95b-53f296d77a0d","79838176-04c9-419e-bf75-3658c220ff34","f64d7ce4-c3b9-43a3-9614-308f1723e8f6","cd9e240f-7860-4e37-a0d2-b922cbcc62d3","7047f880-b929-42fc-81f7-b9dbba2d1b15","fc8c0da8-b043-4406-8758-5b8e68cd815f","2e4ee9a7-b433-43ab-a489-74b4bb5722d0","e5380772-f046-4976-86c6-956d8b8ab863","8a4f3544-015e-4f96-aa49-16857195c34e","f00389e2-c2c7-4447-b3b3-495f91f8a7af","720d9a37-63da-4974-8248-e208e0532859","bf3810c5-13ed-44e2-befa-c71cbe3ab8bb","d74a3ee5-db18-41dc-88dc-452c32d8207f","bce751d4-4e80-4cf2-bb0d-28df2e23ff6b","c8aff11d-7379-430f-bb35-671618327db5","463748a7-b7aa-4d94-8ef6-55b6d772064b","f8ca1fed-e3de-4b0e-a88a-5c6e5aa5ddcd","7b45cd6d-cfc9-43af-aeb1-c83e9858da8f","4a3cf15c-5794-40d7-ab34-093f67dcf33e","3f10519e-0803-44a9-ad95-b208b17e7d2d","a59b6f82-f287-4edc-80d3-f4cb4cdedbba","dab8309d-e9c9-4ac4-ac71-03a7b766c327","70331522-de68-4a8f-aa20-e08d006553ff","3d2ef7bf-2728-4690-adda-13e62fe9b60f","e67968ce-054a-48aa-a8b5-6293d471a9a2","f2f2e18f-174c-4c42-b7bc-99e85ab2e02d","652c001f-d6ad-4c3d-a052-f1169d76b30c","820fcc7d-e70a-4bbd-887c-ed3191a37ca1","a2297d1c-9b74-41e9-92b6-fd5af8d5421e","0cd36a99-8d39-45ec-ad7f-51313c77cdfc","a9595796-5e18-40ca-ba0d-ace42a3eb255","0b89f97e-ceee-4782-8fdc-88bf4d8449a3","820eea06-1421-4df3-97b1-5e21639532e8","62c01166-6f2e-4c75-abd7-2c56eb31c261","3fd62922-5168-4589-b4ca-e365a2f99166","19842565-9739-4535-aa40-d6f36aa201c1","b59320fc-812f-4a9c-885f-d1547296440d","e93ef8cd-2d50-4c9f-9da4-363c739487d4","82247581-c869-4815-a6da-5621b72851a7","46c1bc26-dd2e-4eac-bae9-5d18d315b528","af286828-eb90-46b1-9a91-9e757aa6869e","7b0a3c39-6fd9-4e12-9286-2cf6ecb920fd","1822a750-996b-462b-9096-972738764c77","0637b682-c64e-4f35-8271-60f99c16e83d","aa73ce99-35f2-478f-9b6a-5b3740194143","dd3142e4-237b-46e0-a5fe-7e5c68ee1dfe","beb57526-334c-426a-af9b-9082be53f030","dc585a17-bfd5-4b44-aa7b-ff326abf45d0","0e431270-5f1e-4b2e-ad08-bb15909ecd3c","cb58ac47-7e2e-482d-b1ca-8f04641c9ad9","1bccf186-0695-4fed-8dd4-937a2660c927","7fea355e-0a14-4c64-af2f-4b6b96a7823d","076bd99e-869b-4253-a2fa-5c3697227615","515f6786-1505-44a2-b216-2e22f31ab992","56edff77-1933-4e62-845f-8399e3ade466","a667469d-5eb1-470d-ad93-e37086f187af","2d5df3ac-636f-4ad5-98c3-e4c7cf6f4782","f8e5f193-0b6e-4ff3-91c8-84a3e06f7f75","99c62c8f-0885-401e-8aeb-8e41dd9f4a0b","c8328edf-30eb-429a-8c95-3713ebf8cd5f","6ef1bf66-4143-4290-8cf1-ae4d7a3aa911","315531c6-bd48-4789-9c34-8a52d5f85f6a","a5228f9e-aaef-4835-8413-f6c4e89063aa","09e6fee5-8d20-4fb8-b636-7e92335fa3b2","3cee56ec-4841-43e3-a7fd-afdd4d1fea2e","db064f00-5f8f-447f-ae4c-ccf46aabea1e","d3cadad5-bdd6-4a77-abc5-c752d9a56234","4fd9f2ad-0515-41c9-a2e5-468b315bc707","1051a25f-5609-40d1-9801-10c3b6fd74ab","937e3189-ebcb-4deb-a25b-836ef83b2727","afd112d1-e7e8-4e21-81fa-f00490b4e0ef","e31fb77b-3623-4c65-ac86-760a2248fc1b","d6095301-08c1-4709-88b2-d6bfa30933cb","967d38cd-e0ff-4bfa-96d6-8fec245ff7af","75ad6edd-1f7f-409e-8554-616f46cd939a","aa9cb2d0-a6d6-4fb7-bb02-9298235128b2","fcf62a0c-3481-49f1-b2b0-78b7d5e86123","4b906909-58f6-4582-b092-474bacace127","57a2bfde-d9ad-43aa-ba68-00858a0d83ae","1af546ef-c7c3-437c-8dac-70b80e51a2d6","2598dc29-2e4b-49fb-bec6-db4cb6cde68f","8d9a0671-a39c-4592-861d-2184531fbd8c","1975615d-78d2-4a66-9c13-b513939c6160","0d5228b4-5891-4740-9b13-1b8898ba4957","dfad56a0-6f69-437b-bc50-28195039a9e2","a04a5e94-a584-4f03-b0c5-ea7acf0a28d7","aaeb7e1e-e2ea-445d-8c86-6d5eff7d45ad","d5383d2c-ad69-489e-983d-938bc5356ecf","34c3a6f2-adf4-4c1a-9e47-7fd9dc4f093d","4274a6a6-743e-4853-bce0-ab617f83bcf2","ba164852-9869-45e9-bdfc-7c773724ccab","8b7e59d1-1db2-4a46-91ed-93a04243d94e","1c073184-15ad-4672-a49b-5330be0f397c","e81f9e85-995d-4ffa-a0da-edc10fe4a327","d9cabe01-9c29-45b0-a071-0cd8d80fcb41","7e97054b-cf92-49ec-9f68-54b095f5436e","caa95b8f-86c3-4ec4-9397-933d880aba3e","1d70fdcd-915d-4524-9ab5-1bdda1790508","c0592626-62a8-48f6-93b0-1e4f8ff671f3","fd591b88-4071-4d68-8f5b-d9802036d877","a0ee5172-4912-40a0-846d-7aed4de3c6c2","dfcd8657-9c6c-401c-b0fa-a12506ae5971","977f5abb-7cef-4344-a77d-cbde8461732f","633442c7-b32a-4681-8cf1-0bfe211fed2b","b2ff9394-9d71-453e-adc2-e53d0b91d2bc","0b056d73-92e6-4d78-9948-5f14aa397127","ac6e1e3c-f299-4cfb-bd9e-4c9679702db9","417f1481-2783-4b70-9bba-6d8dc4d873e0","41b0f660-75ee-4d5a-b6a6-412e89330793","43897bbf-1a8d-4b5b-aa9b-8d8380e912c0","76fe4243-bac0-4a7b-8799-acdfe6affa13","97db90d6-c5f9-46b5-bae0-dfe8368673ae","adbed9a8-29a6-4adb-8a5b-60619fa02c19","2e418adc-f3f2-4d49-bc4d-dcbe82dc5a17","dead117a-53c3-40f8-879b-40c170b68037","82bf4caa-944a-4024-ab46-69aca67591ab","0400d1fc-85b4-4808-89bf-cc2c9b88a3e3","007798ee-ffeb-4191-8c58-16df6c125440","be74692c-bc08-416c-8fac-d35a0b798605","c213828e-3d30-46a4-9245-485c9f78c233","a91b1a4e-c90f-446d-bcd2-4afcaff8ee3d","a6a1098d-5064-4a25-ba81-a1b8cd913e28","79bea1b6-8d57-4839-9456-8abf5ea058f6","959e1093-4b1d-4151-9089-9df3e284cbc1","c6e5327e-2ad5-4813-a2f2-107b13b8101b","16059f2a-45ca-4208-8e0c-a8417746c47e","705ea4ff-9780-4865-928f-b651ea0cacbb","ffcb4db7-ba65-42fe-990a-8813564544f9","4130c905-fde5-41bb-b363-8ef9551c8202","75399647-a578-43af-81fa-b83dbaeb991e","a2fd59aa-a304-490a-a496-4ec021b41a16","e2e8dc4d-9ad2-45d4-9ae4-411388b725d3","7b1d2c97-de39-4f98-b896-b6c3e78cba1e","b84fabbc-d296-4efa-8629-e393fb27e21f","ddf6e09c-f018-4048-a69f-436ff22308b5","2cd70c1e-955d-428e-86cd-3efc5ecbcabd","75d80cad-5279-4d78-9410-10001a29ae21","8b898a9c-e7a0-4cac-906c-9264be4e5fbf","3d2e4053-26d7-4f86-b88a-93011ae1725f","290750ca-3d80-43b5-adbd-d6c12692572a","1304ac7c-7acb-4df9-864d-fc911fc00028","955fa978-f0a6-4252-bd6d-22b16fba3c1e","e2a0dc12-9af9-4b2d-9b4f-d89463021560","390a1b51-71f9-4fb8-b053-f01f4d06dbb6","3d16500c-87be-431a-93e6-d517906bd20a","06cd622c-ba03-45ec-a65f-96536a14aece","cadd16ac-d717-4503-98de-794a1f26c219","ffc5a747-4344-4327-ba66-46297d8c8ba4","d1ee9e1a-c2a3-4548-86b5-49fe6268d45b","e564a30c-3c9f-464e-b3b7-20509a7f2b72","4fb94e2e-f366-451d-b5d7-29c4055b219b","41e61c2b-fcf6-4786-bb3c-6d05e57633d6","b60ced1d-e098-4da9-82ab-05b9e818096f","75d2b8e3-8976-49f9-a6b3-dfe87c86b415","25eead5d-73e7-4abd-aa48-5256001f6863","0a1432dd-532b-4320-8396-1226ae148981","6d4349d3-06a0-4b4d-84e0-9457d27c52b2","9ef2d44f-12cc-40ed-8882-224616212774","e91a356e-fa02-4b5a-b719-94039c5aaa42","65a4fb0d-503f-4ce5-961e-e55234ff2b88","677e0452-e795-4299-8e57-ba68a49f0ad0","483e6ca8-293d-4d00-b71b-4464c093a71d","a597b7df-a17f-4e91-ab6c-538cade307bf","507da602-9e74-4f9f-a387-df0286734ec2","2d7308a2-6d7e-4a1d-b271-8348bfe01782","962a52d7-51ff-4312-b2cb-6cdd0a5256aa","e91214ad-0d19-43b4-9586-609f7e818ebc","dabd5a72-02b0-4ce7-95b1-e8657b170dee","e2155e85-f730-4f6c-99a3-4b3a6b664b7a","c34c30ab-ae45-4004-9dc7-926d5d0ed862","b4fb2f5a-2d8a-4bd7-a547-e6699aa6e592","6004bc9f-22fd-4367-b4b2-0b84bd5b2b27","5539c878-497d-4a77-a173-e709e5b589f2","6b32c9a7-164f-4dbd-a21a-363be348784d","67beee79-d8ef-4d97-a3e8-555bb88cd498","c54af629-5136-448d-8caa-75014923991c","79dbd582-a278-46b6-be2e-da002e9e392d","7635ade9-6d01-4116-afb8-18f530e5a0d0","83ca0f48-e251-4bb1-8285-dc5b34082fc2","46e53ea3-3ec0-493d-8b4c-3d8fc5cfbccb","abe0f3c6-ebab-4d55-87a4-7a51e0a12533","ffe1ad0d-cc47-4933-a8cc-f3557770be1d","07591214-cebf-4c34-9b90-dffe3fdef7ad","b03735a2-e537-4583-b69a-75bad77a5381","e4dc23da-746f-49e6-83dc-cbd34271118e","d0f575ab-e355-495c-853d-5336cd188611","60f4284f-9cbb-4b49-8b28-c7ca5101484d","96ae1050-172f-440e-9214-449bb2e36984","0aa01478-c6b6-41d9-8a14-12d772303828","a046f495-25d8-4f3c-9d3b-d4b09981bc80","ebea61de-addc-4179-b17b-f3c4afbdd210","e03b47c8-1554-4d1d-86bf-f09a17d53f65","3fa634b6-88b6-418c-b3d5-7f4bf35ef879","d412a97a-bf37-4a37-a26e-4adf9106de97","3bcea2e6-14ee-4522-bc0b-e39759f3e1f0","68c50fa1-aea3-4baa-a749-40539223bbdc","9d082349-7e00-43ca-b79a-922b648f6874","dc286de6-8a6b-4cc1-aec1-d316d5290384","3f14abca-beaf-4a32-92a8-693243c84e1d","e325c566-56d1-41ca-ab02-5cae7b827456","e0454d97-330d-45c3-b406-f1b5bf4158ae","c5a59d21-aae6-4a15-9732-13a51ec58d26","04e4d0d6-d7a3-43c3-a920-7fb593539975","03e90dc0-9aae-4017-aefc-87be6c16ea35","910d67fd-78db-4b93-aec7-303d03241941","e513429f-b390-494d-86f8-1c5bed0f180f","676b7e98-d74b-4062-b499-7530402a6b6e","1063e358-8a6d-47a3-bfb7-3857546530e2","f470a709-d411-4535-904d-ba743db0af50","6ffc466f-b088-487e-85ae-7c053e25cd9f","9c1159ff-aabd-435f-b96c-5cc7493a8110","addb813d-c274-4947-8d75-29f322bbe547","31bf09b0-0370-102d-b0e3-001ec94a0cc1","47a92a60-588e-4ee4-80a7-83ee919792b9","56a27e54-4dd2-4fde-83d8-44761a3a4213","e9ecc46c-5c0c-4ae8-9480-105173f0bd1d","aa777c47-2dbd-47b3-a440-ccd5d531ce13","c63610b8-09b3-49e4-8ec5-90ea8bb6567f","4395f061-a374-4f52-99ca-2beabc4b64d4","19354709-be47-4f11-b27a-1a41bb55342d","48a2fc8d-4b38-4787-a170-dee58dfd0316","52c45523-db94-4f0d-af12-8df40015de3a","1f411719-0731-4032-8a99-e89760604a18","6da0c77a-3f83-4191-bb53-43bde0effe60","b07bc998-a91d-46e8-be18-a32a1f315711","80667bb0-ad83-4019-b460-bd480a2f549c","f979dd67-566a-47ef-8d6c-64ea1b041108","1b61d5a5-a3a5-490a-ad14-4c8916e21292","8b54d18e-c525-4067-8be7-179ff384847e","9d9cdf9a-5462-486d-bb7c-8c7a35e79139","e8b2e3b7-40f9-4c35-b098-0204c52be32a","e05a9252-c2e4-48de-a335-8d65de02e41e","d46303ed-e0c3-437e-8501-a327620b96c1","e194a5a1-5632-413f-8a7a-f93430978e2e","bdac7716-d85b-4d12-a589-e04570644c26","e0dc69e4-c4c0-46c1-b524-ed2bca46005e","ccd094e6-ac27-418f-a30e-54e9a1bac362","61d48834-7268-4161-ae86-57b46fbce98d","59289c40-8f75-4cee-a5f2-90cbc66fa0e6","888df023-7167-45ba-a71c-f040da19f50d","fdbc018e-be36-4e2d-b39d-d38cba605b7a","c07c7a76-ae79-4d2e-8389-2d753bff6462","4aa77954-ab98-4ad5-bba8-a63625cffb1f","807d1419-2636-4dbe-b22a-1a66fb490ddf","9f1c2f3c-9b61-463b-ae8d-1298a1ada403","742d9630-1190-483c-8bc6-1e8d50cdd543","5763d98b-1650-445a-9f07-24b82d497cc7","ff0e6557-5249-45f3-8cd3-8bb4bc3f1920","f4189170-7947-4b92-948b-8a423ac1f511","7c7f6bb0-6ca2-4d2b-b241-6bd85239a192","15010c1f-0a38-4534-9cbe-09fd36788edc","f2c64b8e-4830-471c-ad52-cab19a9c16e2","377edada-16cf-428c-b3c3-29a1fa7d4640","600701c5-fa64-4625-8914-a966efb24888","42b9dbf0-0ffc-4831-914f-4a51f5913c01","8051c13f-74ea-4975-8164-f721cf1bdbbe","1c030450-2e75-442b-bb78-751e18ec1b26","0597203e-36ad-41a2-b084-5807dc112cb4","f4a2d5c7-dbb2-4925-a0c7-52bb6118c28c","9903423f-14b7-4749-bbb3-f13d09d7f6cd","6cd3396c-5bb8-4dc9-8ca4-50b13c36031c","f2c87c0d-09c3-481f-b652-6ce26c780da2","78d9b0d5-51a8-41ad-abfe-5c452ff9737e","c31c3fe0-c262-4b60-ba5b-217fec063d05","5cb0542d-9f26-4bcd-a548-e1a14162e5f1","4d141fa2-e5d3-4994-a469-ec76302df33a","0922bb2d-4199-44e5-87df-2a7f4dcdad1b","2f391849-5aa4-48bc-956c-db11e9707343","206cd6c6-2967-4fc6-b8b0-5823f37947dc","ccba57c3-6d07-11ee-af2b-e86a64440f18","418bd7fa-bb72-43ca-b07f-e25014ca3541","746532ff-e36a-47bc-8a6e-7d434f533fdd","946ee522-4132-4b50-9dd6-c1aed0fb0449","45a1b710-00cf-4697-af09-5b2269478a20","858d209d-55c2-418c-82e0-fb4f4057c5db","54d88677-b3cb-4f42-88be-5ce657a8c23e","1c041723-8632-47f3-81de-769274dc4c00","8181afee-dfc1-4488-9e02-c8a1c3d819ee","51a1bb37-cd63-43e9-89f6-2833044f383d","0f7abf6d-e0bb-46ce-aa69-5214b0d2a295","4ce4d85b-a5f7-4e0a-ab42-24ebb8778086","207a0630-f0af-4208-9a81-326b8c37ebe2","53aaa4c7-49c0-4716-9af0-9098d35bacc5","8d53d5ad-5477-4997-aadb-5c661c51b5e4","57e8ebf0-67d8-4ae8-a566-e3e5d90a1aa8","31b4bdc0-0370-102d-b0e3-001ec94a0cc1" };
			List<String> notExists = new ArrayList<>();
			for (String uuid : uuids) {
				String endpoint = "/concept/" + uuid;
				Response response = RestAssured.given()
				        .auth().basic(USERNAME, PASSWORD)
				        .when()
				        .get(endpoint);
				JsonPath jsonPath = response.jsonPath();
				String displayValue = jsonPath.get("display");
				if (displayValue == null) {
					notExists.add(uuid);
				}
			}
			for (String s : notExists) {
				System.out.println(s);
			}
		}
	*/
	
	/** Endpoints **/
	@Test
	public void testForm8ReportEndpoint() {
		Map<String, Object> params = new HashMap<>();
		params.put("location", "8cac8e81-6baf-4f6d-88e5-92dd794fded3");
		params.put("year", LocalDate.now().getYear());
		params.put("month", LocalDate.now().getMonthValue());
		String endpoint = "/mdrtb/form8report";
		given().auth().basic(USERNAME, PASSWORD).queryParams(params).when().get(endpoint).then().assertThat()
		        .statusCode(200);
	}
	
	@Test
	public void testForm89ReportEndpoint() {
		Map<String, Object> params = new HashMap<>();
		params.put("location", "8cac8e81-6baf-4f6d-88e5-92dd794fded3");
		params.put("year", LocalDate.now().getYear());
		params.put("month", LocalDate.now().getMonthValue());
		String endpoint = "/mdrtb/form89report";
		given().auth().basic(USERNAME, PASSWORD).queryParams(params).when().get(endpoint).then().assertThat()
		        .statusCode(200);
	}
	
	@Test
	public void testTB03ReportEndpoint() {
		Map<String, Object> params = new HashMap<>();
		params.put("location", "8cac8e81-6baf-4f6d-88e5-92dd794fded3");
		params.put("year", LocalDate.now().getYear());
		params.put("month", LocalDate.now().getMonthValue());
		String endpoint = "/mdrtb/tb03report";
		given().auth().basic(USERNAME, PASSWORD).queryParams(params).when().get(endpoint).then().assertThat()
		        .statusCode(200);
	}
	
	@Test
	public void testTB03uReportEndpoint() {
		Map<String, Object> params = new HashMap<>();
		params.put("location", "8cac8e81-6baf-4f6d-88e5-92dd794fded3");
		params.put("year", LocalDate.now().getYear());
		params.put("month", LocalDate.now().getMonthValue());
		String endpoint = "/mdrtb/tb03ureport";
		given().auth().basic(USERNAME, PASSWORD).queryParams(params).when().get(endpoint).then().assertThat()
		        .statusCode(200);
	}
	
	@Test
	public void testTB07ReportEndpoint() {
		Map<String, Object> params = new HashMap<>();
		params.put("location", "8cac8e81-6baf-4f6d-88e5-92dd794fded3");
		params.put("year", LocalDate.now().getYear());
		params.put("month", LocalDate.now().getMonthValue());
		String endpoint = "/mdrtb/tb07report";
		given().auth().basic(USERNAME, PASSWORD).queryParams(params).when().get(endpoint).then().assertThat()
		        .statusCode(200);
	}
	
	@Test
	public void testTB07uReportEndpoint() {
		Map<String, Object> params = new HashMap<>();
		params.put("location", "8cac8e81-6baf-4f6d-88e5-92dd794fded3");
		params.put("year", LocalDate.now().getYear());
		params.put("month", LocalDate.now().getMonthValue());
		String endpoint = "/mdrtb/tb07ureport";
		given().auth().basic(USERNAME, PASSWORD).queryParams(params).when().get(endpoint).then().assertThat()
		        .statusCode(200);
	}
	
	@Test
	public void testTB08ReportEndpoint() {
		Map<String, Object> params = new HashMap<>();
		params.put("location", "8cac8e81-6baf-4f6d-88e5-92dd794fded3");
		params.put("year", LocalDate.now().getYear());
		params.put("month", LocalDate.now().getMonthValue());
		String endpoint = "/mdrtb/tb08report";
		given().auth().basic(USERNAME, PASSWORD).queryParams(params).when().get(endpoint).then().assertThat()
		        .statusCode(200);
	}
	
	@Test
	public void testTB08uReportEndpoint() {
		Map<String, Object> params = new HashMap<>();
		params.put("location", "8cac8e81-6baf-4f6d-88e5-92dd794fded3");
		params.put("year", LocalDate.now().getYear());
		params.put("month", LocalDate.now().getMonthValue());
		String endpoint = "/mdrtb/tb08ureport";
		given().auth().basic(USERNAME, PASSWORD).queryParams(params).when().get(endpoint).then().assertThat()
		        .statusCode(200);
	}
	
	@Test
	public void testTB03MissingReportEndpoint() {
		Map<String, Object> params = new HashMap<>();
		params.put("location", "8cac8e81-6baf-4f6d-88e5-92dd794fded3");
		params.put("year", LocalDate.now().getYear());
		params.put("month", LocalDate.now().getMonthValue());
		String endpoint = "/mdrtb/tb03missingreport";
		given().auth().basic(USERNAME, PASSWORD).queryParams(params).when().get(endpoint).then()
		        .assertThat().statusCode(200);
	}
	
	@Test
	public void testTB03uMissingReportEndpoint() {
		Map<String, Object> params = new HashMap<>();
		params.put("location", "8cac8e81-6baf-4f6d-88e5-92dd794fded3");
		params.put("year", LocalDate.now().getYear());
		params.put("month", LocalDate.now().getMonthValue());
		String endpoint = "/mdrtb/tb03umissingreport";
		given().auth().basic(USERNAME, PASSWORD).queryParams(params).when().get(endpoint).then()
		        .assertThat().statusCode(200);
	}
	
	@Test
	public void testPatientListEndpoint() {
		Map<String, Object> params = new HashMap<>();
		params.put("location", "8cac8e81-6baf-4f6d-88e5-92dd794fded3");
		params.put("year", LocalDate.now().getYear());
		params.put("month", LocalDate.now().getMonthValue());
		String endpoint = "/mdrtb/patientlist";
		given().auth().basic(USERNAME, PASSWORD).queryParams(params).when().get(endpoint).then().assertThat()
		        .statusCode(200);
	}
	
	/** Report Data **/
	@Test
	public void testForm8Report() {
		Map<String, Object> params = new HashMap<>();
		params.put("location", "8cac8e81-6baf-4f6d-88e5-92dd794fded3");
		params.put("year", LocalDate.now().getYear());
		params.put("quarter", "1");
		String endpoint = "/mdrtb/form8report";
		given().auth().basic(USERNAME, PASSWORD).queryParams(params).when().get(endpoint).then().assertThat()
		        .statusCode(200)
		        .body("results[0].simpleForm8Table1Data.uuid", matchesPattern(UUID_REGEX))
		        .body("results[0].simpleForm8Table2Data.uuid", matchesPattern(UUID_REGEX))
		        .body("results[0].simpleForm8Table3Data.uuid", matchesPattern(UUID_REGEX))
		        .body("results[0].simpleForm8Table4Data.uuid", matchesPattern(UUID_REGEX))
		        .body("results[0].simpleForm8Table5aData.uuid", matchesPattern(UUID_REGEX))
		        .body("results[0].simpleTB08Data.newAllDetected", greaterThan(0));
	}
	
	@Test
	public void testForm89Report() {
		Map<String, Object> params = new HashMap<>();
		params.put("location", "82be00a0-894b-42aa-812f-428f23e9fd7a");
		params.put("year", LocalDate.now().getYear());
		params.put("quarter", "1");
		String endpoint = "/mdrtb/form89report";
		given().auth().basic(USERNAME, PASSWORD).queryParams(params).when().get(endpoint).then().assertThat()
		        .statusCode(200).body("results[0].ageAtTB03Registration", is(notNullValue()));
	}
	
	@Test
	public void testTB03Report() {
		Map<String, Object> params = new HashMap<>();
		params.put("location", "82be00a0-894b-42aa-812f-428f23e9fd7a");
		params.put("year", LocalDate.now().getYear());
		params.put("quarter", "1");
		String endpoint = "/mdrtb/tb03report";
		given().auth().basic(USERNAME, PASSWORD).queryParams(params).when().get(endpoint).then().assertThat()
		        .statusCode(200)
		        .body("results[0].patientUuid", matchesPattern(UUID_REGEX))
		        .body("results[0].identifier", is(notNullValue()));
	}
	
	@Test
	public void testTB03uReport() {
		Map<String, Object> params = new HashMap<>();
		params.put("location", "8cac8e81-6baf-4f6d-88e5-92dd794fded3");
		params.put("year", LocalDate.now().getYear());
		params.put("quarter", "1");
		String endpoint = "/mdrtb/tb03ureport";
		given().auth().basic(USERNAME, PASSWORD).queryParams(params).when().get(endpoint).then().assertThat()
		        .statusCode(200)
		        .body("results[0].dotsYear", greaterThan(0))
		        .body("results[0].identifierMDR", is(notNullValue()));
	}
	
	@Test
	public void testTB07Report() {
		Map<String, Object> params = new HashMap<>();
		params.put("location", "82be00a0-894b-42aa-812f-428f23e9fd7a");
		params.put("year", LocalDate.now().getYear());
		params.put("quarter", "1");
		String endpoint = "/mdrtb/tb07report";
		given().auth().basic(USERNAME, PASSWORD).queryParams(params).when().get(endpoint).then().assertThat()
		        .statusCode(200)
		        .body("results[0].totalAll", greaterThanOrEqualTo(0));
	}
	
	@Test
	public void testTB07uReport() {
		Map<String, Object> params = new HashMap<>();
		params.put("location", "8cac8e81-6baf-4f6d-88e5-92dd794fded3");
		params.put("year", LocalDate.now().getYear());
		params.put("quarter", "1");
		String endpoint = "/mdrtb/tb07ureport";
		given().auth().basic(USERNAME, PASSWORD).queryParams(params).when().get(endpoint).then().assertThat()
		        .statusCode(200)
		        .body("results[0].totalDetections", greaterThanOrEqualTo(0));
	}
	
	@Test
	public void testTB08Report() {
		Map<String, Object> params = new HashMap<>();
		params.put("location", "82be00a0-894b-42aa-812f-428f23e9fd7a");
		params.put("year", LocalDate.now().getYear());
		params.put("quarter", "1");
		String endpoint = "/mdrtb/tb08report";
		given().auth().basic(USERNAME, PASSWORD).queryParams(params).when().get(endpoint).then().assertThat()
		        .statusCode(200)
		        .body("results[0].newAllDetected", greaterThanOrEqualTo(0));
	}
	
	@Test
	public void testTB08uReport() {
		Map<String, Object> params = new HashMap<>();
		params.put("location", "7a27b5db-f952-4396-989f-2623b2ea735d");
		params.put("year", LocalDate.now().getYear());
		params.put("quarter", "1");
		String endpoint = "/mdrtb/tb08ureport";
		given().auth().basic(USERNAME, PASSWORD).queryParams(params).when().get(endpoint).then().assertThat()
		        .statusCode(200)
		        .body("results[0].newTotalShort", greaterThanOrEqualTo(0));
	}
	
	@Test
	public void testTransferOutReport() {
		Map<String, Object> params = new HashMap<>();
		params.put("q", "bb167f6b-baf4-437a-93c1-376231fa78b4");
		String endpoint = "/mdrtb/transferout";
		given().auth().basic(USERNAME, PASSWORD).queryParams(params).when().get(endpoint).then().assertThat()
		        .statusCode(200)
		        .body("results[0].uuid", matchesPattern(UUID_REGEX))
		        .body("results[0].encounter", is(notNullValue()));
	}
	
	@Test
	public void testRegimenReport() {
		Map<String, Object> params = new HashMap<>();
		String endpoint = "/mdrtb/transferout/777130b4-d711-49c1-be1e-e87fbecf17d6";
		given().auth().basic(USERNAME, PASSWORD).queryParams(params).when().get(endpoint).then().assertThat()
		        .statusCode(200)
		        .body("uuid", matchesPattern(UUID_REGEX))
		        .body("encounter", is(notNullValue()));
	}
}
