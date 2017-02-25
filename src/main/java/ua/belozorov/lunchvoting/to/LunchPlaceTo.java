package ua.belozorov.lunchvoting.to;

import lombok.*;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.SafeHtml;
import ua.belozorov.lunchvoting.util.ExceptionUtils;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

/**
 *
 * Created on 21.11.16.
 */
@Getter
@ToString(doNotUseGetters = true)
public final class LunchPlaceTo {

    @NotBlank(groups = Create.class)
    @SafeHtml
    @Size(min = 2, max=50)
    private final String name;

    @SafeHtml
    @Size(max=200)
    private final String address;

    @SafeHtml
    @Size(max=1000)
    private final String description;

    @Valid
    @Size(max = 5)
    private final Set<Phone> phones;

    protected LunchPlaceTo() {
        name = null;
        address = null;
        description = null;
        phones = Collections.emptySet();
    }

    public LunchPlaceTo(String name, String address, String description, Set<String> phones) {
        this.name = name;
        this.address = address;
        this.description = description;
        this.phones = ofNullable(phones.stream().map(Phone::new).collect(Collectors.toSet()))
                .orElse(Collections.emptySet());
    }

    public Set<String> getPhones() {
        return this.phones.stream().map(Phone::getPhone).collect(Collectors.toSet());
    }

    public interface Create {}

    /**
     *
     * Created on 22.02.17.
     */
    private static class Phone {

        @Pattern(regexp = "^[0-9]{10}$")
        private String phone;

        public Phone() {
        }

        public Phone(String phone) {
            this.phone = phone;
        }

        public String getPhone() {
            return phone;
        }
    }
}
