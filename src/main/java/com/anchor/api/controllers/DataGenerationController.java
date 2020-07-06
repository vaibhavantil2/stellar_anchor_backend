package com.anchor.api.controllers;

import com.anchor.api.data.anchor.Anchor;
import com.anchor.api.data.stokvel.Member;
import com.anchor.api.data.stokvel.Stokvel;
import com.anchor.api.services.AccountService;
import com.anchor.api.services.AgentService;
import com.anchor.api.services.AnchorAccountService;
import com.anchor.api.services.TOMLService;
import com.anchor.api.util.DemoDataGenerator;
import com.anchor.api.util.Emoji;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moandjiezana.toml.Toml;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

@CrossOrigin(maxAge = 3600)
@RestController
public class DataGenerationController {
    public static final Logger LOGGER = Logger.getLogger(AnchorController.class.getSimpleName());
    private static final Gson G = new GsonBuilder().setPrettyPrinting().create();
    @Autowired
    private ApplicationContext context;
    @Autowired
    private AnchorAccountService anchorAccountService;
    @Autowired
    private AgentService agentService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private DemoDataGenerator demoDataGenerator;
    @Autowired
    private TOMLService tomlService;
    @Value("${status}")
    private String status;


    @GetMapping(value = "/generateAnchor", produces = MediaType.APPLICATION_JSON_VALUE)
    public Anchor generateAnchor(String anchorName) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 StellarAnchorApplication /generateAnchor ...");
        Anchor anchor = demoDataGenerator.startAnchor(anchorName);
        LOGGER.info(Emoji.DICE.concat(Emoji.DICE.concat(Emoji.DICE)
                .concat("New Anchor Returned")
                .concat(G.toJson(anchor))));
        LOGGER.info( "\uD83D\uDC99 \uD83D\uDC9C GenerateDemoData:generateAnchor completed and returning external caller... "
                + new Date().toString() + " \uD83D\uDC99 \uD83D\uDC9C STATUS: " + status);

        LOGGER.info("\n\n \uD83C\uDF4E \uD83C\uDF4E \uD83C\uDF4E \uD83C\uDF4E");
        LOGGER.info( "\uD83D\uDC99 \uD83D\uDC9C ################################################################## \uD83D\uDC99 \uD83D\uDC9C");
        LOGGER.info( "\uD83D\uDC99 \uD83D\uDC9C PREPARE STELLAR.TOML and ANCHOR.TOML - edit ANCHOR ID, ISSUING ACCOUNTS etc.  \uD83D\uDC99 \uD83D\uDC9C");
        LOGGER.info( "\uD83D\uDC99 \uD83D\uDC9C UPLOAD STELLAR.TOML and ANCHOR.TOML to cloud storage               \uD83D\uDC99 \uD83D\uDC9C");
        LOGGER.info( "\uD83D\uDC99 \uD83D\uDC9C Retrieve account info from Firestore or logs                       \uD83D\uDC99 \uD83D\uDC9C");
        LOGGER.info( "\uD83D\uDC99 \uD83D\uDC9C ################################################################## \uD83D\uDC99 \uD83D\uDC9C");
        LOGGER.info("\n\n \uD83C\uDF4E \uD83C\uDF4E \uD83C\uDF4E \uD83C\uDF4E");

        return anchor;
    }
    @GetMapping(value = "/generateDemo", produces = MediaType.TEXT_PLAIN_VALUE)
    public String generateDemo(@RequestParam String anchorId) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 StellarAnchorApplication /generateDemo ...");
        checkTOML(anchorId);
        demoDataGenerator.startGeneration(anchorId);
        return "\n\n\uD83D\uDC99 \uD83D\uDC9C GenerateDemoData completed ... "
                + new Date().toString() + " \uD83D\uDC99 \uD83D\uDC9C STATUS: " + status;
    }

    private void checkTOML(@RequestParam String anchorId) throws Exception {
        Toml toml = tomlService.getAnchorToml(anchorId);
        if (toml == null) {
            LOGGER.severe(Emoji.ERROR+Emoji.ERROR+Emoji.ERROR+ " Missing anchor.toml, QUITTING! - " +
                    "Please upload an anchor.toml first before trying this again. " + Emoji.ERROR+Emoji.ERROR);
            throw new Exception("anchor.toml file missing");
        }
    }

    @GetMapping(value = "/generateAgentClients", produces = MediaType.TEXT_PLAIN_VALUE)
    public String generateAgentClients(@RequestParam String anchorId, @RequestParam int count) throws Exception {
        checkTOML(anchorId);
        demoDataGenerator.generateAgentClients(anchorId, count);
        return "\uD83D\uDC99 \uD83D\uDC9C generateAgentClients completed ... "
                + new Date().toString() + " \uD83D\uDC99 \uD83D\uDC9C STATUS: " + status;
    }
    @GetMapping(value = "/generateLoans", produces = MediaType.TEXT_PLAIN_VALUE)
    public String generateLoans(@RequestParam String anchorId) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 StellarAnchorApplication /generateLoans ...");
        demoDataGenerator.generateLoanApplications(anchorId);
        return "\uD83D\uDC99 \uD83D\uDC9C GenerateLoans completed ... "
                + new Date().toString() + " \uD83D\uDC99 \uD83D\uDC9C STATUS: " + status;
    }
    @GetMapping(value = "/generateStokvel", produces = MediaType.APPLICATION_JSON_VALUE)
    public Stokvel generateStokvel(@RequestParam String anchorId) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 StellarAnchorApplication /generateStokvel ...");
        Stokvel stokvel = demoDataGenerator.generateStokvel(anchorId);
        String msg =  "\uD83D\uDC99 \uD83D\uDC9C GenerateStokvel completed ... "
                + stokvel.getName() + " \uD83D\uDC99 \uD83D\uDC9C STATUS: " + status;
        LOGGER.info(msg);
        return stokvel;
    }
    @GetMapping(value = "/generateClients", produces = MediaType.APPLICATION_JSON_VALUE)
    public String generateClients(@RequestParam String anchorId, @RequestParam int count) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 StellarAnchorApplication /generateClients ..." +
                "" + anchorId + "  count: " + count);
        checkTOML(anchorId);
        demoDataGenerator.generateAgentClients(anchorId, count);
        String msg =  "\uD83D\uDC99 \uD83D\uDC9C generateAgentClients completed ... \uD83C\uDF3C \uD83C\uDF3C "
                + anchorId + " \uD83D\uDC99 \uD83D\uDC9C STATUS: " + status;
        LOGGER.info(msg);
        return msg;
    }
    @GetMapping(value = "/generateStokvelMembers", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Member> generateStokvelMembers(@RequestParam String anchorId, @RequestParam String stokvelId) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 StellarAnchorApplication /generateStokvelMembers ...");
        List<Member> stokvel = demoDataGenerator.generateStokvelMembers(stokvelId, anchorId);
        String msg =  "\uD83D\uDC99 \uD83D\uDC9C GenerateStokvelMembers completed ... "
                + stokvel + " \uD83D\uDC99 \uD83D\uDC9C STATUS: " + status;
        LOGGER.info(msg);
        return stokvel;
    }
    @GetMapping(value = "/generateAgentFunding", produces = MediaType.TEXT_PLAIN_VALUE)
    public String generateAgentFunding(@RequestParam String anchorId) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 StellarAnchorApplication /generateAgentFunding ...");
        demoDataGenerator.generateAgentFunding(anchorId);
        return "\uD83D\uDC99 \uD83D\uDC9C GenerateAgentFunding completed ... "
                + new Date().toString() + " \uD83D\uDC99 \uD83D\uDC9C STATUS: " + status;
    }
    @GetMapping(value = "/generateLoanApprovals", produces = MediaType.TEXT_PLAIN_VALUE)
    public String generateLoanApprovals(@RequestParam String anchorId) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 StellarAnchorApplication /generateLoanApprovals ...");
        demoDataGenerator.generateLoanApprovals(anchorId);
        return "\uD83D\uDC99 \uD83D\uDC9C GenerateLoanApprovals completed ... "
                + new DateTime().toDateTimeISO().toString() + " \uD83D\uDC99 STATUS: " + status;
    }
    @GetMapping(value = "/generatePayments", produces = MediaType.TEXT_PLAIN_VALUE)
    public String generatePayments(@RequestParam String anchorId) throws Exception {
        LOGGER.info("\uD83D\uDD35 \uD83D\uDD35 \uD83D\uDD35 StellarAnchorApplication /generatePayments ...");
        demoDataGenerator.generatePayments(anchorId);
        return "\uD83D\uDC99 \uD83D\uDC9C GeneratePayments completed ... "
                + new DateTime().toDateTimeISO().toString() + " \uD83D\uDC99 STATUS: " + status;
    }
}
