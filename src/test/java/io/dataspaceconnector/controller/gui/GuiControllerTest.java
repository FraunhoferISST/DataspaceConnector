/*
 * Copyright 2020 Fraunhofer Institute for Software and Systems Engineering
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.dataspaceconnector.controller.gui;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for the GuiUtilController class.
 */
@SpringBootTest
class GuiControllerTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    @Test
    void unauthorizedGetEnum() throws Exception {
        mockMvc.perform(get("/api/utils/enums")).andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    void getEnums() throws Exception {
        /* ARRANGE */
        final var response = "{\"LANGUAGE\":[{\"originalName\":\"PA\",\"displayName\":\"(Eastern)" +
                " Punjabi\",\"jsonInput\":\"PA\"},{\"originalName\":\"AB\"," +
                "\"displayName\":\"Abkhaz\",\"jsonInput\":\"AB\"},{\"originalName\":\"AA\"," +
                "\"displayName\":\"Afar\",\"jsonInput\":\"AA\"},{\"originalName\":\"AF\"," +
                "\"displayName\":\"Afrikaans\",\"jsonInput\":\"AF\"},{\"originalName\":\"AK\"," +
                "\"displayName\":\"Akan\",\"jsonInput\":\"AK\"},{\"originalName\":\"SQ\"," +
                "\"displayName\":\"Albanian\",\"jsonInput\":\"SQ\"},{\"originalName\":\"AM\"," +
                "\"displayName\":\"Amharic\",\"jsonInput\":\"AM\"},{\"originalName\":\"AR\"," +
                "\"displayName\":\"Arabic\",\"jsonInput\":\"AR\"},{\"originalName\":\"AN\"," +
                "\"displayName\":\"Aragonese\",\"jsonInput\":\"AN\"},{\"originalName\":\"HY\"," +
                "\"displayName\":\"Armenian\",\"jsonInput\":\"HY\"},{\"originalName\":\"AS\"," +
                "\"displayName\":\"Assamese\",\"jsonInput\":\"AS\"},{\"originalName\":\"AV\"," +
                "\"displayName\":\"Avaric\",\"jsonInput\":\"AV\"},{\"originalName\":\"AE\"," +
                "\"displayName\":\"Avestan\",\"jsonInput\":\"AE\"},{\"originalName\":\"AY\"," +
                "\"displayName\":\"Aymara\",\"jsonInput\":\"AY\"},{\"originalName\":\"AZ\"," +
                "\"displayName\":\"Azerbaijani\",\"jsonInput\":\"AZ\"},{\"originalName\":\"BM\"," +
                "\"displayName\":\"Bambara\",\"jsonInput\":\"BM\"},{\"originalName\":\"BA\"," +
                "\"displayName\":\"Bashkir\",\"jsonInput\":\"BA\"},{\"originalName\":\"EU\"," +
                "\"displayName\":\"Basque\",\"jsonInput\":\"EU\"},{\"originalName\":\"BE\"," +
                "\"displayName\":\"Belarusian\",\"jsonInput\":\"BE\"},{\"originalName\":\"BN\"," +
                "\"displayName\":\"Bengali, Bangla\",\"jsonInput\":\"BN\"}," +
                "{\"originalName\":\"BH\",\"displayName\":\"Bihari\",\"jsonInput\":\"BH\"}," +
                "{\"originalName\":\"BI\",\"displayName\":\"Bislama\",\"jsonInput\":\"BI\"}," +
                "{\"originalName\":\"BS\",\"displayName\":\"Bosnian\",\"jsonInput\":\"BS\"}," +
                "{\"originalName\":\"BR\",\"displayName\":\"Breton\",\"jsonInput\":\"BR\"}," +
                "{\"originalName\":\"BG\",\"displayName\":\"Bulgarian\",\"jsonInput\":\"BG\"}," +
                "{\"originalName\":\"MY\",\"displayName\":\"Burmese\",\"jsonInput\":\"MY\"}," +
                "{\"originalName\":\"CA\",\"displayName\":\"Catalan\",\"jsonInput\":\"CA\"}," +
                "{\"originalName\":\"CH\",\"displayName\":\"Chamorro\",\"jsonInput\":\"CH\"}," +
                "{\"originalName\":\"CE\",\"displayName\":\"Chechen\",\"jsonInput\":\"CE\"}," +
                "{\"originalName\":\"NY\",\"displayName\":\"Chichewa, Chewa, Nyanja\"," +
                "\"jsonInput\":\"NY\"},{\"originalName\":\"ZH\",\"displayName\":\"Chinese\"," +
                "\"jsonInput\":\"ZH\"},{\"originalName\":\"CV\",\"displayName\":\"Chuvash\"," +
                "\"jsonInput\":\"CV\"},{\"originalName\":\"KW\",\"displayName\":\"Cornish\"," +
                "\"jsonInput\":\"KW\"},{\"originalName\":\"CO\",\"displayName\":\"Corsican\"," +
                "\"jsonInput\":\"CO\"},{\"originalName\":\"CR\",\"displayName\":\"Cree\"," +
                "\"jsonInput\":\"CR\"},{\"originalName\":\"HR\",\"displayName\":\"Croatian\"," +
                "\"jsonInput\":\"HR\"},{\"originalName\":\"CS\",\"displayName\":\"Czech\"," +
                "\"jsonInput\":\"CS\"},{\"originalName\":\"DA\",\"displayName\":\"Danish\"," +
                "\"jsonInput\":\"DA\"},{\"originalName\":\"DV\",\"displayName\":\"Divehi, " +
                "Dhivehi, Maldivian\",\"jsonInput\":\"DV\"},{\"originalName\":\"NL\"," +
                "\"displayName\":\"Dutch\",\"jsonInput\":\"NL\"},{\"originalName\":\"DZ\"," +
                "\"displayName\":\"Dzongkha\",\"jsonInput\":\"DZ\"},{\"originalName\":\"EN\"," +
                "\"displayName\":\"English\",\"jsonInput\":\"EN\"},{\"originalName\":\"EO\"," +
                "\"displayName\":\"Esperanto\",\"jsonInput\":\"EO\"},{\"originalName\":\"ET\"," +
                "\"displayName\":\"Estonian\",\"jsonInput\":\"ET\"},{\"originalName\":\"EE\"," +
                "\"displayName\":\"Ewe\",\"jsonInput\":\"EE\"},{\"originalName\":\"FO\"," +
                "\"displayName\":\"Faroese\",\"jsonInput\":\"FO\"},{\"originalName\":\"FJ\"," +
                "\"displayName\":\"Fijian\",\"jsonInput\":\"FJ\"},{\"originalName\":\"FI\"," +
                "\"displayName\":\"Finnish\",\"jsonInput\":\"FI\"},{\"originalName\":\"FR\"," +
                "\"displayName\":\"French\",\"jsonInput\":\"FR\"},{\"originalName\":\"FF\"," +
                "\"displayName\":\"Fula, Fulah, Pulaar, Pular\",\"jsonInput\":\"FF\"}," +
                "{\"originalName\":\"GL\",\"displayName\":\"Galician\",\"jsonInput\":\"GL\"}," +
                "{\"originalName\":\"LG\",\"displayName\":\"Ganda\",\"jsonInput\":\"LG\"}," +
                "{\"originalName\":\"KA\",\"displayName\":\"Georgian\",\"jsonInput\":\"KA\"}," +
                "{\"originalName\":\"DE\",\"displayName\":\"German\",\"jsonInput\":\"DE\"}," +
                "{\"originalName\":\"EL\",\"displayName\":\"Greek (modern)\"," +
                "\"jsonInput\":\"EL\"},{\"originalName\":\"GN\"," +
                "\"displayName\":\"GuaranÃ\u00AD\",\"jsonInput\":\"GN\"}," +
                "{\"originalName\":\"GU\",\"displayName\":\"Gujarati\",\"jsonInput\":\"GU\"}," +
                "{\"originalName\":\"HT\",\"displayName\":\"Haitian, Haitian Creole\"," +
                "\"jsonInput\":\"HT\"},{\"originalName\":\"HA\",\"displayName\":\"Hausa\"," +
                "\"jsonInput\":\"HA\"},{\"originalName\":\"HE\",\"displayName\":\"Hebrew (modern)" +
                "\",\"jsonInput\":\"HE\"},{\"originalName\":\"HZ\",\"displayName\":\"Herero\"," +
                "\"jsonInput\":\"HZ\"},{\"originalName\":\"HI\",\"displayName\":\"Hindi\"," +
                "\"jsonInput\":\"HI\"},{\"originalName\":\"HO\",\"displayName\":\"Hiri Motu\"," +
                "\"jsonInput\":\"HO\"},{\"originalName\":\"HU\",\"displayName\":\"Hungarian\"," +
                "\"jsonInput\":\"HU\"},{\"originalName\":\"IS\",\"displayName\":\"Icelandic\"," +
                "\"jsonInput\":\"IS\"},{\"originalName\":\"IO\",\"displayName\":\"Ido\"," +
                "\"jsonInput\":\"IO\"},{\"originalName\":\"IG\",\"displayName\":\"Igbo\"," +
                "\"jsonInput\":\"IG\"},{\"originalName\":\"ID\",\"displayName\":\"Indonesian\"," +
                "\"jsonInput\":\"ID\"},{\"originalName\":\"IA\",\"displayName\":\"Interlingua\"," +
                "\"jsonInput\":\"IA\"},{\"originalName\":\"IE\",\"displayName\":\"Interlingue\"," +
                "\"jsonInput\":\"IE\"},{\"originalName\":\"IU\",\"displayName\":\"Inuktitut\"," +
                "\"jsonInput\":\"IU\"},{\"originalName\":\"IK\",\"displayName\":\"Inupiaq\"," +
                "\"jsonInput\":\"IK\"},{\"originalName\":\"GA\",\"displayName\":\"Irish\"," +
                "\"jsonInput\":\"GA\"},{\"originalName\":\"IT\",\"displayName\":\"Italian\"," +
                "\"jsonInput\":\"IT\"},{\"originalName\":\"JA\",\"displayName\":\"Japanese\"," +
                "\"jsonInput\":\"JA\"},{\"originalName\":\"JV\",\"displayName\":\"Javanese\"," +
                "\"jsonInput\":\"JV\"},{\"originalName\":\"KL\",\"displayName\":\"Kalaallisut, " +
                "Greenlandic\",\"jsonInput\":\"KL\"},{\"originalName\":\"KN\"," +
                "\"displayName\":\"Kannada\",\"jsonInput\":\"KN\"},{\"originalName\":\"KR\"," +
                "\"displayName\":\"Kanuri\",\"jsonInput\":\"KR\"},{\"originalName\":\"KS\"," +
                "\"displayName\":\"Kashmiri\",\"jsonInput\":\"KS\"},{\"originalName\":\"KK\"," +
                "\"displayName\":\"Kazakh\",\"jsonInput\":\"KK\"},{\"originalName\":\"KM\"," +
                "\"displayName\":\"Khmer\",\"jsonInput\":\"KM\"},{\"originalName\":\"KI\"," +
                "\"displayName\":\"Kikuyu, Gikuyu\",\"jsonInput\":\"KI\"}," +
                "{\"originalName\":\"RW\",\"displayName\":\"Kinyarwanda\",\"jsonInput\":\"RW\"}," +
                "{\"originalName\":\"RN\",\"displayName\":\"Kirundi\",\"jsonInput\":\"RN\"}," +
                "{\"originalName\":\"KV\",\"displayName\":\"Komi\",\"jsonInput\":\"KV\"}," +
                "{\"originalName\":\"KG\",\"displayName\":\"Kongo\",\"jsonInput\":\"KG\"}," +
                "{\"originalName\":\"KO\",\"displayName\":\"Korean\",\"jsonInput\":\"KO\"}," +
                "{\"originalName\":\"KU\",\"displayName\":\"Kurdish\",\"jsonInput\":\"KU\"}," +
                "{\"originalName\":\"KJ\",\"displayName\":\"Kwanyama, Kuanyama\"," +
                "\"jsonInput\":\"KJ\"},{\"originalName\":\"KY\",\"displayName\":\"Kyrgyz\"," +
                "\"jsonInput\":\"KY\"},{\"originalName\":\"LO\",\"displayName\":\"Lao\"," +
                "\"jsonInput\":\"LO\"},{\"originalName\":\"LA\",\"displayName\":\"Latin\"," +
                "\"jsonInput\":\"LA\"},{\"originalName\":\"LV\",\"displayName\":\"Latvian\"," +
                "\"jsonInput\":\"LV\"},{\"originalName\":\"LI\",\"displayName\":\"Limburgish, " +
                "Limburgan, Limburger\",\"jsonInput\":\"LI\"},{\"originalName\":\"LN\"," +
                "\"displayName\":\"Lingala\",\"jsonInput\":\"LN\"},{\"originalName\":\"LT\"," +
                "\"displayName\":\"Lithuanian\",\"jsonInput\":\"LT\"},{\"originalName\":\"LU\"," +
                "\"displayName\":\"Luba-Katanga\",\"jsonInput\":\"LU\"},{\"originalName\":\"LB\"," +
                "\"displayName\":\"Luxembourgish, Letzeburgesch\",\"jsonInput\":\"LB\"}," +
                "{\"originalName\":\"MK\",\"displayName\":\"Macedonian\",\"jsonInput\":\"MK\"}," +
                "{\"originalName\":\"MG\",\"displayName\":\"Malagasy\",\"jsonInput\":\"MG\"}," +
                "{\"originalName\":\"MS\",\"displayName\":\"Malay\",\"jsonInput\":\"MS\"}," +
                "{\"originalName\":\"ML\",\"displayName\":\"Malayalam\",\"jsonInput\":\"ML\"}," +
                "{\"originalName\":\"MT\",\"displayName\":\"Maltese\",\"jsonInput\":\"MT\"}," +
                "{\"originalName\":\"GV\",\"displayName\":\"Manx\",\"jsonInput\":\"GV\"}," +
                "{\"originalName\":\"MR\",\"displayName\":\"Marathi (MarÄ\u0081á¹\u00ADhÄ«)\"," +
                "\"jsonInput\":\"MR\"},{\"originalName\":\"MH\",\"displayName\":\"Marshallese\"," +
                "\"jsonInput\":\"MH\"},{\"originalName\":\"MN\",\"displayName\":\"Mongolian\"," +
                "\"jsonInput\":\"MN\"},{\"originalName\":\"MULTI_LINGUAL\"," +
                "\"displayName\":\"Multilingual\",\"jsonInput\":\"MULTI_LINGUAL\"}," +
                "{\"originalName\":\"MI\",\"displayName\":\"MÄ\u0081ori\",\"jsonInput\":\"MI\"}," +
                "{\"originalName\":\"NA\",\"displayName\":\"Nauruan\",\"jsonInput\":\"NA\"}," +
                "{\"originalName\":\"NV\",\"displayName\":\"Navajo, Navaho\"," +
                "\"jsonInput\":\"NV\"},{\"originalName\":\"NG\",\"displayName\":\"Ndonga\"," +
                "\"jsonInput\":\"NG\"},{\"originalName\":\"NE\",\"displayName\":\"Nepali\"," +
                "\"jsonInput\":\"NE\"},{\"originalName\":\"ND\",\"displayName\":\"Northern " +
                "Ndebele\",\"jsonInput\":\"ND\"},{\"originalName\":\"SE\"," +
                "\"displayName\":\"Northern Sami\",\"jsonInput\":\"SE\"}," +
                "{\"originalName\":\"NO\",\"displayName\":\"Norwegian\",\"jsonInput\":\"NO\"}," +
                "{\"originalName\":\"NB\",\"displayName\":\"Norwegian BokmÃ¥l\"," +
                "\"jsonInput\":\"NB\"},{\"originalName\":\"NN\",\"displayName\":\"Norwegian " +
                "Nynorsk\",\"jsonInput\":\"NN\"},{\"originalName\":\"II\"," +
                "\"displayName\":\"Nuosu\",\"jsonInput\":\"II\"},{\"originalName\":\"OC\"," +
                "\"displayName\":\"Occitan\",\"jsonInput\":\"OC\"},{\"originalName\":\"OJ\"," +
                "\"displayName\":\"Ojibwe, Ojibwa\",\"jsonInput\":\"OJ\"}," +
                "{\"originalName\":\"CU\",\"displayName\":\"Old Church Slavonic, Church Slavonic," +
                " Old Bulgarian\",\"jsonInput\":\"CU\"},{\"originalName\":\"OR\"," +
                "\"displayName\":\"Oriya\",\"jsonInput\":\"OR\"},{\"originalName\":\"OM\"," +
                "\"displayName\":\"Oromo\",\"jsonInput\":\"OM\"},{\"originalName\":\"OS\"," +
                "\"displayName\":\"Ossetian, Ossetic\",\"jsonInput\":\"OS\"}," +
                "{\"originalName\":\"PS\",\"displayName\":\"Pashto, Pushto\"," +
                "\"jsonInput\":\"PS\"},{\"originalName\":\"FA\",\"displayName\":\"Persian (Farsi)" +
                "\",\"jsonInput\":\"FA\"},{\"originalName\":\"PL\",\"displayName\":\"Polish\"," +
                "\"jsonInput\":\"PL\"},{\"originalName\":\"PT\",\"displayName\":\"Portuguese\"," +
                "\"jsonInput\":\"PT\"},{\"originalName\":\"PI\",\"displayName\":\"PÄ\u0081li\"," +
                "\"jsonInput\":\"PI\"},{\"originalName\":\"QU\",\"displayName\":\"Quechua\"," +
                "\"jsonInput\":\"QU\"},{\"originalName\":\"RO\",\"displayName\":\"Romanian\"," +
                "\"jsonInput\":\"RO\"},{\"originalName\":\"RM\",\"displayName\":\"Romansh\"," +
                "\"jsonInput\":\"RM\"},{\"originalName\":\"RU\",\"displayName\":\"Russian\"," +
                "\"jsonInput\":\"RU\"},{\"originalName\":\"SM\",\"displayName\":\"Samoan\"," +
                "\"jsonInput\":\"SM\"},{\"originalName\":\"SG\",\"displayName\":\"Sango\"," +
                "\"jsonInput\":\"SG\"},{\"originalName\":\"SA\",\"displayName\":\"Sanskrit " +
                "(Saá¹\u0081ská¹\u009Bta)\",\"jsonInput\":\"SA\"},{\"originalName\":\"SC\"," +
                "\"displayName\":\"Sardinian\",\"jsonInput\":\"SC\"},{\"originalName\":\"GD\"," +
                "\"displayName\":\"Scottish Gaelic, Gaelic\",\"jsonInput\":\"GD\"}," +
                "{\"originalName\":\"SR\",\"displayName\":\"Serbian\",\"jsonInput\":\"SR\"}," +
                "{\"originalName\":\"SN\",\"displayName\":\"Shona\",\"jsonInput\":\"SN\"}," +
                "{\"originalName\":\"SD\",\"displayName\":\"Sindhi\",\"jsonInput\":\"SD\"}," +
                "{\"originalName\":\"SI\",\"displayName\":\"Sinhalese, Sinhala\"," +
                "\"jsonInput\":\"SI\"},{\"originalName\":\"SK\",\"displayName\":\"Slovak\"," +
                "\"jsonInput\":\"SK\"},{\"originalName\":\"SL\",\"displayName\":\"Slovene\"," +
                "\"jsonInput\":\"SL\"},{\"originalName\":\"SO\",\"displayName\":\"Somali\"," +
                "\"jsonInput\":\"SO\"},{\"originalName\":\"NR\",\"displayName\":\"Southern " +
                "Ndebele\",\"jsonInput\":\"NR\"},{\"originalName\":\"ST\"," +
                "\"displayName\":\"Southern Sotho\",\"jsonInput\":\"ST\"}," +
                "{\"originalName\":\"ES\",\"displayName\":\"Spanish\",\"jsonInput\":\"ES\"}," +
                "{\"originalName\":\"SU\",\"displayName\":\"Sundanese\",\"jsonInput\":\"SU\"}," +
                "{\"originalName\":\"SW\",\"displayName\":\"Swahili\",\"jsonInput\":\"SW\"}," +
                "{\"originalName\":\"SS\",\"displayName\":\"Swati\",\"jsonInput\":\"SS\"}," +
                "{\"originalName\":\"SV\",\"displayName\":\"Swedish\",\"jsonInput\":\"SV\"}," +
                "{\"originalName\":\"TL\",\"displayName\":\"Tagalog\",\"jsonInput\":\"TL\"}," +
                "{\"originalName\":\"TY\",\"displayName\":\"Tahitian\",\"jsonInput\":\"TY\"}," +
                "{\"originalName\":\"TG\",\"displayName\":\"Tajik\",\"jsonInput\":\"TG\"}," +
                "{\"originalName\":\"TA\",\"displayName\":\"Tamil\",\"jsonInput\":\"TA\"}," +
                "{\"originalName\":\"TT\",\"displayName\":\"Tatar\",\"jsonInput\":\"TT\"}," +
                "{\"originalName\":\"TE\",\"displayName\":\"Telugu\",\"jsonInput\":\"TE\"}," +
                "{\"originalName\":\"TH\",\"displayName\":\"Thai\",\"jsonInput\":\"TH\"}," +
                "{\"originalName\":\"BO\",\"displayName\":\"Tibetan Standard, Tibetan, Central\"," +
                "\"jsonInput\":\"BO\"},{\"originalName\":\"TI\",\"displayName\":\"Tigrinya\"," +
                "\"jsonInput\":\"TI\"},{\"originalName\":\"TO\",\"displayName\":\"Tonga (Tonga " +
                "Islands)\",\"jsonInput\":\"TO\"},{\"originalName\":\"TS\"," +
                "\"displayName\":\"Tsonga\",\"jsonInput\":\"TS\"},{\"originalName\":\"TN\"," +
                "\"displayName\":\"Tswana\",\"jsonInput\":\"TN\"},{\"originalName\":\"TR\"," +
                "\"displayName\":\"Turkish\",\"jsonInput\":\"TR\"},{\"originalName\":\"TK\"," +
                "\"displayName\":\"Turkmen\",\"jsonInput\":\"TK\"},{\"originalName\":\"TW\"," +
                "\"displayName\":\"Twi\",\"jsonInput\":\"TW\"},{\"originalName\":\"UK\"," +
                "\"displayName\":\"Ukrainian\",\"jsonInput\":\"UK\"},{\"originalName\":\"UR\"," +
                "\"displayName\":\"Urdu\",\"jsonInput\":\"UR\"},{\"originalName\":\"UG\"," +
                "\"displayName\":\"Uyghur\",\"jsonInput\":\"UG\"},{\"originalName\":\"UZ\"," +
                "\"displayName\":\"Uzbek\",\"jsonInput\":\"UZ\"},{\"originalName\":\"VE\"," +
                "\"displayName\":\"Venda\",\"jsonInput\":\"VE\"},{\"originalName\":\"VI\"," +
                "\"displayName\":\"Vietnamese\",\"jsonInput\":\"VI\"},{\"originalName\":\"VO\"," +
                "\"displayName\":\"VolapÃ¼k\",\"jsonInput\":\"VO\"},{\"originalName\":\"WA\"," +
                "\"displayName\":\"Walloon\",\"jsonInput\":\"WA\"},{\"originalName\":\"CY\"," +
                "\"displayName\":\"Welsh\",\"jsonInput\":\"CY\"},{\"originalName\":\"FY\"," +
                "\"displayName\":\"Western Frisian\",\"jsonInput\":\"FY\"}," +
                "{\"originalName\":\"WO\",\"displayName\":\"Wolof\",\"jsonInput\":\"WO\"}," +
                "{\"originalName\":\"XH\",\"displayName\":\"Xhosa\",\"jsonInput\":\"XH\"}," +
                "{\"originalName\":\"YI\",\"displayName\":\"Yiddish\",\"jsonInput\":\"YI\"}," +
                "{\"originalName\":\"YO\",\"displayName\":\"Yoruba\",\"jsonInput\":\"YO\"}," +
                "{\"originalName\":\"ZA\",\"displayName\":\"Zhuang, Chuang\"," +
                "\"jsonInput\":\"ZA\"},{\"originalName\":\"ZU\",\"displayName\":\"Zulu\"," +
                "\"jsonInput\":\"ZU\"}],\"types\":[\"LOG_LEVEL\",\"CONNECTOR_STATUS\"," +
                "\"CONNECTOR_DEPLOY_MODE\",\"LANGUAGE\",\"DEPLOY_METHOD\",\"BROKER_STATUS\"," +
                "\"SECURITY_PROFILE\",\"PAYMENT_METHOD\",\"POLICY_PATTERN\",\"UPDATE_TYPE\"," +
                "\"ENDPOINT_TYPE\",\"EVENT_TYPE\",\"ERROR_MESSAGE\",\"USAGE_CONTROL_FRAMEWORK\"," +
                "\"ACTION_TYPE\",\"DATA_SOURCE_TYPE\"]," +
                "\"CONNECTOR_STATUS\":[{\"originalName\":\"FAULTY\",\"displayName\":\"Faulty\"," +
                "\"jsonInput\":\"Faulty\"},{\"originalName\":\"OFFLINE\"," +
                "\"displayName\":\"Offline\",\"jsonInput\":\"Offline\"}," +
                "{\"originalName\":\"ONLINE\",\"displayName\":\"Online\"," +
                "\"jsonInput\":\"Online\"}],\"ERROR_MESSAGE\":[{\"originalName\":\"NOT_ALLOWED\"," +
                "\"displayName\":\"Access is not allowed.\"},{\"originalName\":\"RDF_FAILED\"," +
                "\"displayName\":\"Could not retrieve rdf string.\"}," +
                "{\"originalName\":\"DATA_ACCESS_INVALID_CONSUMER\",\"displayName\":\"Data access" +
                " by invalid consumer connector.\"}," +
                "{\"originalName\":\"DATA_ACCESS_INVALID_INTERVAL\",\"displayName\":\"Data access" +
                " in invalid time interval.\"}," +
                "{\"originalName\":\"DATA_ACCESS_INVALID_SECURITY_PROFILE\"," +
                "\"displayName\":\"Data access with invalid security profile.\"}," +
                "{\"originalName\":\"EMTPY_ENTITY\",\"displayName\":\"Element could not be found" +
                ".\"},{\"originalName\":\"EMPTY_CONTRACT\",\"displayName\":\"Empty contracts " +
                "cannot be compared.\"},{\"originalName\":\"MESSAGE_BUILDING_FAILED\"," +
                "\"displayName\":\"Failed to build ids message.\"}," +
                "{\"originalName\":\"UPDATE_MESSAGE_FAILED\",\"displayName\":\"Failed to send " +
                "update message.\"},{\"originalName\":\"GATEWAY_TIMEOUT\"," +
                "\"displayName\":\"Gateway timeout when connecting to recipient.\"}," +
                "{\"originalName\":\"INVALID_DAT\",\"displayName\":\"Invalid DAT in incoming " +
                "response message.\"},{\"originalName\":\"INVALID_INPUT\"," +
                "\"displayName\":\"Invalid input, processing failed.\"}," +
                "{\"originalName\":\"MALFORMED_PAYLOAD\",\"displayName\":\"Malformed message " +
                "payload.\"},{\"originalName\":\"MESSAGE_HANDLING_FAILED\"," +
                "\"displayName\":\"Message handling or processing failed.\"}," +
                "{\"originalName\":\"MESSAGE_SENDING_FAILED\",\"displayName\":\"Message sending " +
                "failed.\"},{\"originalName\":\"MISSING_PAYLOAD\",\"displayName\":\"Missing " +
                "message payload.\"},{\"originalName\":\"UNKNOWN_TYPE\",\"displayName\":\"No " +
                "behavior has been defined for this type.\"}," +
                "{\"originalName\":\"NO_REQUEST_CONTEXT\",\"displayName\":\"No request context " +
                "present for extracting base URL.\"},{\"originalName\":\"POLICY_RESTRICTION\"," +
                "\"displayName\":\"Policy restriction detected.\"}," +
                "{\"originalName\":\"RESPONSE_NULL\",\"displayName\":\"Received an empty response" +
                " for http request.\"},{\"originalName\":\"INVALID_MESSAGE\"," +
                "\"displayName\":\"Received invalid ids message.\"}," +
                "{\"originalName\":\"MISSING_SECURITY_PROFILE_CLAIM\",\"displayName\":\"The DAT " +
                "of the issuer connector is missing attributes. Cannot enforce security " +
                "restricted policy. Access denied.\"},{\"originalName\":\"CONTRACT_NULL\"," +
                "\"displayName\":\"The contract may not be null.\"}," +
                "{\"originalName\":\"CONTRACT_MISMATCH\",\"displayName\":\"The contract's content" +
                " do not match.\"},{\"originalName\":\"DAT_NULL\",\"displayName\":\"The dat may " +
                "not be null.\"},{\"originalName\":\"DESC_NULL\",\"displayName\":\"The " +
                "description parameter may not be null.\"},{\"originalName\":\"ENTITYID_NULL\"," +
                "\"displayName\":\"The entity id may not be null.\"}," +
                "{\"originalName\":\"ENTITY_NULL\",\"displayName\":\"The entity may not be null" +
                ".\"},{\"originalName\":\"EXCEPTION_NULL\",\"displayName\":\"The exception " +
                "parameter may not be null.\"},{\"originalName\":\"MESSAGE_NULL\"," +
                "\"displayName\":\"The message may not be null.\"}," +
                "{\"originalName\":\"PAGEABLE_NULL\",\"displayName\":\"The pageable parameter may" +
                " not be null.\"},{\"originalName\":\"AUTH_NULL\",\"displayName\":\"The passed " +
                "authentication may not be null.\"},{\"originalName\":\"HTTP_ARGS_NULL\"," +
                "\"displayName\":\"The passed http arguments may not be null.\"}," +
                "{\"originalName\":\"LIST_NULL\",\"displayName\":\"The passed list may not be " +
                "null.\"},{\"originalName\":\"URI_NULL\",\"displayName\":\"The passed uri may not" +
                " be null.\"},{\"originalName\":\"URL_NULL\",\"displayName\":\"The passed url may" +
                " not be null.\"},{\"originalName\":\"CERTIFICATE_NOT_TRUSTED\"," +
                "\"displayName\":\"The recipient's certificate authority is not trusted.\"}," +
                "{\"originalName\":\"ENTITYSET_NULL\",\"displayName\":\"The set of entities may " +
                "not be null.\"},{\"originalName\":\"VERSION_NULL\",\"displayName\":\"The version" +
                " must be set.\"},{\"originalName\":\"DATA_ACCESS_NUMBER_REACHED\"," +
                "\"displayName\":\"Valid access number reached.\"}]," +
                "\"EVENT_TYPE\":[{\"originalName\":\"UPDATED\",\"displayName\":\"Updated\"}]," +
                "\"PAYMENT_METHOD\":[{\"originalName\":\"FIXED_PRICE\",\"displayName\":\"Fixed " +
                "price\",\"jsonInput\":\"fixedPrice\"},{\"originalName\":\"FREE\"," +
                "\"displayName\":\"Free\",\"jsonInput\":\"free\"}," +
                "{\"originalName\":\"NEGOTIATION_BASIS\",\"displayName\":\"Negotiation basis\"," +
                "\"jsonInput\":\"negotiationBasis\"},{\"originalName\":\"UNDEFINED\"," +
                "\"displayName\":\"Undefined\",\"jsonInput\":\"undefined\"}]," +
                "\"POLICY_PATTERN\":[{\"originalName\":\"USAGE_LOGGING\",\"displayName\":\"Log " +
                "the data usage information\",\"jsonInput\":\"USAGE_LOGGING\"}," +
                "{\"originalName\":\"USAGE_NOTIFICATION\",\"displayName\":\"Notify a party or a " +
                "specific group of users when the data is used\"," +
                "\"jsonInput\":\"USAGE_NOTIFICATION\"},{\"originalName\":\"PROHIBIT_ACCESS\"," +
                "\"displayName\":\"Prohibit the data access\",\"jsonInput\":\"PROHIBIT_ACCESS\"}," +
                "{\"originalName\":\"PROVIDE_ACCESS\",\"displayName\":\"Provide unrestricted data" +
                " access\",\"jsonInput\":\"PROVIDE_ACCESS\"}," +
                "{\"originalName\":\"SECURITY_PROFILE_RESTRICTED_USAGE\"," +
                "\"displayName\":\"Restrict the data usage to a security profile\"," +
                "\"jsonInput\":\"SECURITY_PROFILE_RESTRICTED_USAGE\"}," +
                "{\"originalName\":\"CONNECTOR_RESTRICTED_USAGE\",\"displayName\":\"Restrict the " +
                "data usage to a specific connector\"," +
                "\"jsonInput\":\"CONNECTOR_RESTRICTED_USAGE\"}," +
                "{\"originalName\":\"DURATION_USAGE\",\"displayName\":\"Restrict the data usage " +
                "to a specific time duration\",\"jsonInput\":\"DURATION_USAGE\"}," +
                "{\"originalName\":\"USAGE_DURING_INTERVAL\",\"displayName\":\"Restrict the data " +
                "usage to a specific time interval\",\"jsonInput\":\"USAGE_DURING_INTERVAL\"}," +
                "{\"originalName\":\"N_TIMES_USAGE\",\"displayName\":\"Restrict the data usage to" +
                " not more than N times\",\"jsonInput\":\"N_TIMES_USAGE\"}," +
                "{\"originalName\":\"USAGE_UNTIL_DELETION\",\"displayName\":\"Use data and delete" +
                " it at a specific date time\",\"jsonInput\":\"USAGE_UNTIL_DELETION\"}]," +
                "\"SECURITY_PROFILE\":[{\"originalName\":\"BASE_SECURITY\",\"displayName\":\"Base" +
                " Security\"},{\"originalName\":\"TRUST_PLUS_SECURITY\",\"displayName\":\"Trust " +
                "Plus Security\"},{\"originalName\":\"TRUST_SECURITY\",\"displayName\":\"Trust " +
                "Security\"}],\"BROKER_STATUS\":[{\"originalName\":\"REGISTERED\"," +
                "\"displayName\":\"Registered\"},{\"originalName\":\"UNREGISTERED\"," +
                "\"displayName\":\"Unregistered\"}],\"ENDPOINT_TYPE\":[{\"originalName\":\"APP\"," +
                "\"displayName\":\"App\",\"jsonInput\":\"APP\"},{\"originalName\":\"CONNECTOR\"," +
                "\"displayName\":\"Connector\",\"jsonInput\":\"CONNECTOR\"}," +
                "{\"originalName\":\"GENERIC\",\"displayName\":\"Generic\"," +
                "\"jsonInput\":\"GENERIC\"}],\"USAGE_CONTROL_FRAMEWORK\":[{\"originalName" +
                "\":\"INTERNAL\",\"displayName\":\"Internal\"},{\"originalName\":\"MY_DATA\"," +
                "\"displayName\":\"MyData\"}],\"UPDATE_TYPE\":[{\"originalName\":\"MAJOR\"," +
                "\"displayName\":\"Major\"},{\"originalName\":\"MINOR\"," +
                "\"displayName\":\"Minor\"},{\"originalName\":\"NO_UPDATE\"," +
                "\"displayName\":\"None\"},{\"originalName\":\"PATCH\"," +
                "\"displayName\":\"Patch\"}]," +
                "\"CONNECTOR_DEPLOY_MODE\":[{\"originalName\":\"PRODUCTIVE\"," +
                "\"displayName\":\"Productive\"},{\"originalName\":\"TEST\"," +
                "\"displayName\":\"Test\"}],\"LOG_LEVEL\":[{\"originalName\":\"DEBUG\"," +
                "\"displayName\":\"Debug\",\"jsonInput\":\"Debug\"},{\"originalName\":\"ERROR\"," +
                "\"displayName\":\"Error\",\"jsonInput\":\"Error\"},{\"originalName\":\"INFO\"," +
                "\"displayName\":\"Info\",\"jsonInput\":\"Info\"},{\"originalName\":\"OFF\"," +
                "\"displayName\":\"Off\",\"jsonInput\":\"Off\"},{\"originalName\":\"TRACE\"," +
                "\"displayName\":\"Trace\",\"jsonInput\":\"Trace\"},{\"originalName\":\"WARN\"," +
                "\"displayName\":\"Warn\",\"jsonInput\":\"Warn\"}]," +
                "\"DEPLOY_METHOD\":[{\"originalName\":\"CAMEL\",\"displayName\":\"Camel\"," +
                "\"jsonInput\":\"Camel\"},{\"originalName\":\"NONE\",\"displayName\":\"None\"," +
                "\"jsonInput\":\"None\"}],\"DATA_SOURCE_TYPE\":[{\"originalName\":\"DATABASE\"," +
                "\"displayName\":\"Database\",\"jsonInput\":\"DATABASE\"}," +
                "{\"originalName\":\"REST\",\"displayName\":\"REST\",\"jsonInput\":\"REST\"}]," +
                "\"ACTION_TYPE\":[{\"originalName\":\"DELETE\",\"displayName\":\"Delete\"," +
                "\"jsonInput\":\"Delete\"},{\"originalName\":\"DESCRIBE\"," +
                "\"displayName\":\"Describe\",\"jsonInput\":\"Describe\"}," +
                "{\"originalName\":\"START\",\"displayName\":\"Start\",\"jsonInput\":\"Start\"}," +
                "{\"originalName\":\"STOP\",\"displayName\":\"Stop\",\"jsonInput\":\"Stop\"}]}";

        /* ACT */
        final var result = mockMvc.perform(get("/api/utils/enums")).andReturn();

        /* ASSERT */
        assertFalse(result.getResponse().getContentAsString().isEmpty());
        assertEquals(response, result.getResponse().getContentAsString());
        assertEquals(200, result.getResponse().getStatus());
    }
}
