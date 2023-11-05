package com.akasaair.rm_webservice.common.Validators;

import java.util.List;
import java.util.Map;

public interface Validator {
    boolean isValid(List<Map<String, String>> data, String value);
}
