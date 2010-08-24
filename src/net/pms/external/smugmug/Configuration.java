package net.pms.external.smugmug;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.pms.PMS;

//smugmug.accountid1.username=username
//smugmug.accountid1.password=password
//smugmug.accountid1.apikey=asdfasdfasfasdfasdfasdf
//
//smugmug.accountid2.name=Nice Name
//smugmug.accountid2.username=username
//smugmug.accountid2.password=password
//smugmug.accountid2.apikey=asdfasdfasfasdfasdfasdf

public class Configuration {
	final List<Account> account; 
	
	public Configuration(File file) throws ConfigurationException {
		Properties properties = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream(file);
			properties.load(input);
		} catch (IOException e) {
			throw new ConfigurationException("Error reading configuration from file: " + file, e); 
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					PMS.error("Error closing input stream", e);
				}
			}
		}
		account = new LinkedList<Account>();
		for (ParserAccount parserAccount : parse2(parse1(properties))) 
			account.add(new Account(parserAccount.id, parserAccount.apikey, parserAccount.email, parserAccount.password, parserAccount.name));
	}

	public List<Account> getAccount() {
		return Collections.unmodifiableList(account);
	}

	private static final Pattern PATTERN = Pattern.compile("smugmug\\.([^\\.]+)\\.([^\\.]+)", Pattern.CASE_INSENSITIVE);
	
	@SuppressWarnings("rawtypes")
	private Collection<ParserAccount> parse1(Properties properties) throws ConfigurationException {
		Map<String, ParserAccount> account = new HashMap<String, ParserAccount>();
		int sequence = 0;
		for (Entry entry : properties.entrySet()) {
			Matcher matcher = PATTERN.matcher((String) entry.getKey());
			if (! matcher.matches()) {
				throw new ConfigurationException("Encountered invalid account setting: " + entry.getKey());
			}
			String id = matcher.group(1).toLowerCase();
			if (account.get(id) == null) {
				account.put(id, new ParserAccount());
				account.get(id).id = id;
				account.get(id).sequence = sequence++;
			}
			String key = matcher.group(2).toLowerCase();
			String value = (String) entry.getValue();
			if (key.equals("email"))
				account.get(id).email = value ;
			else if (key.equals("password"))
				account.get(id).password = value;
			else if (key.equals("name"))
				account.get(id).name = value;
			else if (key.equals("apikey"))
				account.get(id).apikey = value; 
		}
		return account.values();
	}
	
	private static class ParserAccount {
		public String id;
		public String email;
		public String password;
		public String name;
		public String apikey;
		public int sequence;
		
		boolean isValid() {
			return ! Utils.isEmpty(email) && ! Utils.isEmpty(password) && ! Utils.isEmpty(apikey);
		}
	}
	
	private List<ParserAccount> parse2(Collection<ParserAccount> account) {
		List<ParserAccount> result = new LinkedList<ParserAccount>();
		for (ParserAccount parserAccount : account) {
			if (! parserAccount.isValid()) {
				PMS.error("Account with ID: " + parserAccount.id + " (ignoring)", null);
				continue;
			}
			result.add(parserAccount);
		}
		Collections.sort(result, new Comparator<ParserAccount>() {
			@Override
			public int compare(ParserAccount o1, ParserAccount o2) {
				return o1.sequence - o2.sequence; 			
			}}); 
		return result;
	}
}
