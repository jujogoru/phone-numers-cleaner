import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CleanerPhoneNumbers {
	
	private static final String REGEX_ALPHABETIC_SPECIAL = "([A-Z a-záéíóúñÑÁÉÍÓÚ*!#$%&/()=?¡¿'@.,;:_\\\\+~ü\"{}\\[\\]~^`|<>Çç])+";
	private static final String REGEX_WHITESPACES_PARENTHESIS_HYPHEN = "(\\s|\\(|\\)|-|\\P{Print})+";
	private static final String REGEX_REVERSE_SPLIT_COMMA_SPACE = ", ";
	private static final String REGEX_REVERSE_BRACKETS = "[\\[\\]]";
	private static final String REGEX_INTERNATIONAL_PHONE = "\\+52\\d{10,11}";
	private static final String REGEX_MOBILEPHONE_LOCAL = "044\\d{10}";
	private static final String REGEX_MOBILEPHONE_FOREIGN = "045\\d{10}";
	private static final String REGEX_10_DIGITS_PHONE = "\\d{10}";
	private static final String REGEX_PHONE_FOREIGN = "01\\d{10}";
	private static final String REGEX_CONTACTS_FILE_OUTPUT_EXTENSION = ".csv";
	private static final String REGEX_CONTACTS_FILE_INPUT = "/home/pepito/Documents/contacts.csv";
	private static final String REGEX_CONTACTS_FILE_OUTPUT = "/home/pepito/Documents/cleanedContacts".concat(REGEX_CONTACTS_FILE_OUTPUT_EXTENSION);
	private static final String REGEX_COMMA = ",";	
	private static final String REGEX_POINTS = " ::: ";
	private static final String REGEX_NOTHING = "";
	private static final String CARRIAGE_RETURN = "\n";

	//String wtf = "+52 1 873 747 5905‬"; //After last '5' digit there's an unknown character generated by Google Contacts O.o

	public static void main(String[] args) throws IOException {
		System.out.println("Cleaning phone numbers..." + CARRIAGE_RETURN);

		BufferedWriter writer = null;
		
		try (BufferedReader br = new BufferedReader(new FileReader(REGEX_CONTACTS_FILE_INPUT))) {

			writer = new BufferedWriter(new FileWriter(REGEX_CONTACTS_FILE_OUTPUT, true));
			String line;
			String[] splittedLine = null;
			String[] subSplittedLine = null;
			List<String> cleanedSplittedLine = new ArrayList<>();
			List<String> cleanedSubSplittedElement = new ArrayList<>();

			while ((line = br.readLine()) != null) {

				splittedLine = line.split(REGEX_COMMA, -1);

				for (String element : splittedLine) {
					
					if(element.contains(REGEX_POINTS)) {
						subSplittedLine = element.split(REGEX_POINTS, -1);
						
						for (String subElement : subSplittedLine) {
							subElement = cleanElement(subElement);
							cleanedSubSplittedElement.add(subElement);
						}
						element = reverseSplit(cleanedSubSplittedElement.toArray(new String[0]), REGEX_POINTS);
						cleanedSubSplittedElement.clear();
					}
					else {
						element = cleanElement(element);
					}
					
					cleanedSplittedLine.add(element);
				}
				
				line = reverseSplit(cleanedSplittedLine.toArray(new String[0]), REGEX_COMMA);

				writer.append(line + CARRIAGE_RETURN);
				cleanedSplittedLine.clear();
			}

			writer.close();
			System.out.println("File CSV created succesfuly: " + REGEX_CONTACTS_FILE_OUTPUT);
		} catch (Exception e) {
			System.out.println("There was a problem reading file: " + REGEX_CONTACTS_FILE_INPUT + CARRIAGE_RETURN + e.getMessage());
			e.printStackTrace();
		} 
	}

	private static String cleanElement(String element) {
		String phone;
		
		if (element != null && !element.isEmpty() && (
				element.replaceAll(REGEX_WHITESPACES_PARENTHESIS_HYPHEN, REGEX_NOTHING).matches(REGEX_INTERNATIONAL_PHONE) ||
				element.replaceAll(REGEX_WHITESPACES_PARENTHESIS_HYPHEN, REGEX_NOTHING).matches(REGEX_10_DIGITS_PHONE) ||
				element.replaceAll(REGEX_WHITESPACES_PARENTHESIS_HYPHEN, REGEX_NOTHING).matches(REGEX_MOBILEPHONE_LOCAL) ||
				element.replaceAll(REGEX_WHITESPACES_PARENTHESIS_HYPHEN, REGEX_NOTHING).matches(REGEX_MOBILEPHONE_FOREIGN) ||
				element.replaceAll(REGEX_WHITESPACES_PARENTHESIS_HYPHEN, REGEX_NOTHING).matches(REGEX_PHONE_FOREIGN) 
				)) {
			
			System.out.println("Element to clean: " + element);
			
			element = element.replaceAll(REGEX_WHITESPACES_PARENTHESIS_HYPHEN, REGEX_NOTHING);
			phone = element.substring(element.length() - 10, element.length());

			if (element.matches(REGEX_INTERNATIONAL_PHONE)) {
				element = element.substring(0, 3).concat(phone);
			} else {
				element = phone;
			}

			System.out.println("Phone number result: " + element);
		} else {
			if (!element.isEmpty() && !element.matches(REGEX_ALPHABETIC_SPECIAL)) {
				System.out.println("Not a Mexican number or bad format number: " + element);
			}
		}
		
		return element;
	}

	private static String reverseSplit(String gArreglo[], String delimiter) {
		return Arrays.toString(gArreglo).replace(REGEX_REVERSE_SPLIT_COMMA_SPACE, delimiter).replaceAll(REGEX_REVERSE_BRACKETS, REGEX_NOTHING);
	}
}