package org.ozyegin.cs.entity;

import com.google.common.base.Objects;
import java.util.List;

public class Company {
  private String name;
  private int zip;
  private String country;
  private String city;
  private String streetInfo;
  private String phoneNumber;
  private List<String> e_mails;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getZip() {
    return zip;
  }

  public void setZip(int zip) {
    this.zip = zip;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = city;
  }

  public String getStreetInfo() {
    return streetInfo;
  }

  public void setStreetInfo(String streetInfo) {
    this.streetInfo = streetInfo;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public void setPhoneNumber(String phoneNumber) {
    this.phoneNumber = phoneNumber;
  }

  public List<String> getE_mails() {
    return e_mails;
  }

  public void setE_mails(List<String> e_mails) {
    this.e_mails = e_mails;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Company company = (Company) o;

    for(String email: company.getE_mails()) {
      if(!getE_mails().contains(email))
        return false;
    }

    return getZip() == company.getZip() &&
        Objects.equal(getName(), company.getName()) &&
        Objects.equal(getCountry(), company.getCountry()) &&
        Objects.equal(getCity(), company.getCity()) &&
        Objects.equal(getStreetInfo(), company.getStreetInfo()) &&
        Objects.equal(getPhoneNumber(), company.getPhoneNumber()) &&
        getE_mails().size() == company.getE_mails().size();
  }

  @Override
  public int hashCode() {
    return Objects
        .hashCode(getName(), getZip(), getCountry(), getCity(), getStreetInfo(), getPhoneNumber());
  }
}
