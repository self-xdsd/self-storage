/*
 * This file is generated by jOOQ.
 */
package com.selfxdsd.storage.generated.jooq.tables.records;


import com.selfxdsd.storage.generated.jooq.tables.SlfInvoicedtasksXdsd;

import java.math.BigInteger;
import java.time.LocalDateTime;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record14;
import org.jooq.Row14;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class SlfInvoicedtasksXdsdRecord extends UpdatableRecordImpl<SlfInvoicedtasksXdsdRecord> implements Record14<Integer, Integer, String, String, String, String, BigInteger, String, LocalDateTime, LocalDateTime, LocalDateTime, Integer, BigInteger, Boolean> {

    private static final long serialVersionUID = -1142751644;

    /**
     * Setter for <code>self_xdsd.slf_invoicedtasks_xdsd.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>self_xdsd.slf_invoicedtasks_xdsd.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>self_xdsd.slf_invoicedtasks_xdsd.invoiceId</code>.
     */
    public void setInvoiceid(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>self_xdsd.slf_invoicedtasks_xdsd.invoiceId</code>.
     */
    public Integer getInvoiceid() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>self_xdsd.slf_invoicedtasks_xdsd.repo_fullname</code>.
     */
    public void setRepoFullname(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>self_xdsd.slf_invoicedtasks_xdsd.repo_fullname</code>.
     */
    public String getRepoFullname() {
        return (String) get(2);
    }

    /**
     * Setter for <code>self_xdsd.slf_invoicedtasks_xdsd.username</code>.
     */
    public void setUsername(String value) {
        set(3, value);
    }

    /**
     * Getter for <code>self_xdsd.slf_invoicedtasks_xdsd.username</code>.
     */
    public String getUsername() {
        return (String) get(3);
    }

    /**
     * Setter for <code>self_xdsd.slf_invoicedtasks_xdsd.provider</code>.
     */
    public void setProvider(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>self_xdsd.slf_invoicedtasks_xdsd.provider</code>.
     */
    public String getProvider() {
        return (String) get(4);
    }

    /**
     * Setter for <code>self_xdsd.slf_invoicedtasks_xdsd.role</code>.
     */
    public void setRole(String value) {
        set(5, value);
    }

    /**
     * Getter for <code>self_xdsd.slf_invoicedtasks_xdsd.role</code>.
     */
    public String getRole() {
        return (String) get(5);
    }

    /**
     * Setter for <code>self_xdsd.slf_invoicedtasks_xdsd.value</code>.
     */
    public void setValue(BigInteger value) {
        set(6, value);
    }

    /**
     * Getter for <code>self_xdsd.slf_invoicedtasks_xdsd.value</code>.
     */
    public BigInteger getValue() {
        return (BigInteger) get(6);
    }

    /**
     * Setter for <code>self_xdsd.slf_invoicedtasks_xdsd.issueId</code>.
     */
    public void setIssueid(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>self_xdsd.slf_invoicedtasks_xdsd.issueId</code>.
     */
    public String getIssueid() {
        return (String) get(7);
    }

    /**
     * Setter for <code>self_xdsd.slf_invoicedtasks_xdsd.assigned</code>.
     */
    public void setAssigned(LocalDateTime value) {
        set(8, value);
    }

    /**
     * Getter for <code>self_xdsd.slf_invoicedtasks_xdsd.assigned</code>.
     */
    public LocalDateTime getAssigned() {
        return (LocalDateTime) get(8);
    }

    /**
     * Setter for <code>self_xdsd.slf_invoicedtasks_xdsd.deadline</code>.
     */
    public void setDeadline(LocalDateTime value) {
        set(9, value);
    }

    /**
     * Getter for <code>self_xdsd.slf_invoicedtasks_xdsd.deadline</code>.
     */
    public LocalDateTime getDeadline() {
        return (LocalDateTime) get(9);
    }

    /**
     * Setter for <code>self_xdsd.slf_invoicedtasks_xdsd.invoiced</code>.
     */
    public void setInvoiced(LocalDateTime value) {
        set(10, value);
    }

    /**
     * Getter for <code>self_xdsd.slf_invoicedtasks_xdsd.invoiced</code>.
     */
    public LocalDateTime getInvoiced() {
        return (LocalDateTime) get(10);
    }

    /**
     * Setter for <code>self_xdsd.slf_invoicedtasks_xdsd.estimation_minutes</code>.
     */
    public void setEstimationMinutes(Integer value) {
        set(11, value);
    }

    /**
     * Getter for <code>self_xdsd.slf_invoicedtasks_xdsd.estimation_minutes</code>.
     */
    public Integer getEstimationMinutes() {
        return (Integer) get(11);
    }

    /**
     * Setter for <code>self_xdsd.slf_invoicedtasks_xdsd.commission</code>.
     */
    public void setCommission(BigInteger value) {
        set(12, value);
    }

    /**
     * Getter for <code>self_xdsd.slf_invoicedtasks_xdsd.commission</code>.
     */
    public BigInteger getCommission() {
        return (BigInteger) get(12);
    }

    /**
     * Setter for <code>self_xdsd.slf_invoicedtasks_xdsd.isPullRequest</code>.
     */
    public void setIspullrequest(Boolean value) {
        set(13, value);
    }

    /**
     * Getter for <code>self_xdsd.slf_invoicedtasks_xdsd.isPullRequest</code>.
     */
    public Boolean getIspullrequest() {
        return (Boolean) get(13);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record14 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row14<Integer, Integer, String, String, String, String, BigInteger, String, LocalDateTime, LocalDateTime, LocalDateTime, Integer, BigInteger, Boolean> fieldsRow() {
        return (Row14) super.fieldsRow();
    }

    @Override
    public Row14<Integer, Integer, String, String, String, String, BigInteger, String, LocalDateTime, LocalDateTime, LocalDateTime, Integer, BigInteger, Boolean> valuesRow() {
        return (Row14) super.valuesRow();
    }

    @Override
    public Field<Integer> field1() {
        return SlfInvoicedtasksXdsd.SLF_INVOICEDTASKS_XDSD.ID;
    }

    @Override
    public Field<Integer> field2() {
        return SlfInvoicedtasksXdsd.SLF_INVOICEDTASKS_XDSD.INVOICEID;
    }

    @Override
    public Field<String> field3() {
        return SlfInvoicedtasksXdsd.SLF_INVOICEDTASKS_XDSD.REPO_FULLNAME;
    }

    @Override
    public Field<String> field4() {
        return SlfInvoicedtasksXdsd.SLF_INVOICEDTASKS_XDSD.USERNAME;
    }

    @Override
    public Field<String> field5() {
        return SlfInvoicedtasksXdsd.SLF_INVOICEDTASKS_XDSD.PROVIDER;
    }

    @Override
    public Field<String> field6() {
        return SlfInvoicedtasksXdsd.SLF_INVOICEDTASKS_XDSD.ROLE;
    }

    @Override
    public Field<BigInteger> field7() {
        return SlfInvoicedtasksXdsd.SLF_INVOICEDTASKS_XDSD.VALUE;
    }

    @Override
    public Field<String> field8() {
        return SlfInvoicedtasksXdsd.SLF_INVOICEDTASKS_XDSD.ISSUEID;
    }

    @Override
    public Field<LocalDateTime> field9() {
        return SlfInvoicedtasksXdsd.SLF_INVOICEDTASKS_XDSD.ASSIGNED;
    }

    @Override
    public Field<LocalDateTime> field10() {
        return SlfInvoicedtasksXdsd.SLF_INVOICEDTASKS_XDSD.DEADLINE;
    }

    @Override
    public Field<LocalDateTime> field11() {
        return SlfInvoicedtasksXdsd.SLF_INVOICEDTASKS_XDSD.INVOICED;
    }

    @Override
    public Field<Integer> field12() {
        return SlfInvoicedtasksXdsd.SLF_INVOICEDTASKS_XDSD.ESTIMATION_MINUTES;
    }

    @Override
    public Field<BigInteger> field13() {
        return SlfInvoicedtasksXdsd.SLF_INVOICEDTASKS_XDSD.COMMISSION;
    }

    @Override
    public Field<Boolean> field14() {
        return SlfInvoicedtasksXdsd.SLF_INVOICEDTASKS_XDSD.ISPULLREQUEST;
    }

    @Override
    public Integer component1() {
        return getId();
    }

    @Override
    public Integer component2() {
        return getInvoiceid();
    }

    @Override
    public String component3() {
        return getRepoFullname();
    }

    @Override
    public String component4() {
        return getUsername();
    }

    @Override
    public String component5() {
        return getProvider();
    }

    @Override
    public String component6() {
        return getRole();
    }

    @Override
    public BigInteger component7() {
        return getValue();
    }

    @Override
    public String component8() {
        return getIssueid();
    }

    @Override
    public LocalDateTime component9() {
        return getAssigned();
    }

    @Override
    public LocalDateTime component10() {
        return getDeadline();
    }

    @Override
    public LocalDateTime component11() {
        return getInvoiced();
    }

    @Override
    public Integer component12() {
        return getEstimationMinutes();
    }

    @Override
    public BigInteger component13() {
        return getCommission();
    }

    @Override
    public Boolean component14() {
        return getIspullrequest();
    }

    @Override
    public Integer value1() {
        return getId();
    }

    @Override
    public Integer value2() {
        return getInvoiceid();
    }

    @Override
    public String value3() {
        return getRepoFullname();
    }

    @Override
    public String value4() {
        return getUsername();
    }

    @Override
    public String value5() {
        return getProvider();
    }

    @Override
    public String value6() {
        return getRole();
    }

    @Override
    public BigInteger value7() {
        return getValue();
    }

    @Override
    public String value8() {
        return getIssueid();
    }

    @Override
    public LocalDateTime value9() {
        return getAssigned();
    }

    @Override
    public LocalDateTime value10() {
        return getDeadline();
    }

    @Override
    public LocalDateTime value11() {
        return getInvoiced();
    }

    @Override
    public Integer value12() {
        return getEstimationMinutes();
    }

    @Override
    public BigInteger value13() {
        return getCommission();
    }

    @Override
    public Boolean value14() {
        return getIspullrequest();
    }

    @Override
    public SlfInvoicedtasksXdsdRecord value1(Integer value) {
        setId(value);
        return this;
    }

    @Override
    public SlfInvoicedtasksXdsdRecord value2(Integer value) {
        setInvoiceid(value);
        return this;
    }

    @Override
    public SlfInvoicedtasksXdsdRecord value3(String value) {
        setRepoFullname(value);
        return this;
    }

    @Override
    public SlfInvoicedtasksXdsdRecord value4(String value) {
        setUsername(value);
        return this;
    }

    @Override
    public SlfInvoicedtasksXdsdRecord value5(String value) {
        setProvider(value);
        return this;
    }

    @Override
    public SlfInvoicedtasksXdsdRecord value6(String value) {
        setRole(value);
        return this;
    }

    @Override
    public SlfInvoicedtasksXdsdRecord value7(BigInteger value) {
        setValue(value);
        return this;
    }

    @Override
    public SlfInvoicedtasksXdsdRecord value8(String value) {
        setIssueid(value);
        return this;
    }

    @Override
    public SlfInvoicedtasksXdsdRecord value9(LocalDateTime value) {
        setAssigned(value);
        return this;
    }

    @Override
    public SlfInvoicedtasksXdsdRecord value10(LocalDateTime value) {
        setDeadline(value);
        return this;
    }

    @Override
    public SlfInvoicedtasksXdsdRecord value11(LocalDateTime value) {
        setInvoiced(value);
        return this;
    }

    @Override
    public SlfInvoicedtasksXdsdRecord value12(Integer value) {
        setEstimationMinutes(value);
        return this;
    }

    @Override
    public SlfInvoicedtasksXdsdRecord value13(BigInteger value) {
        setCommission(value);
        return this;
    }

    @Override
    public SlfInvoicedtasksXdsdRecord value14(Boolean value) {
        setIspullrequest(value);
        return this;
    }

    @Override
    public SlfInvoicedtasksXdsdRecord values(Integer value1, Integer value2, String value3, String value4, String value5, String value6, BigInteger value7, String value8, LocalDateTime value9, LocalDateTime value10, LocalDateTime value11, Integer value12, BigInteger value13, Boolean value14) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        value9(value9);
        value10(value10);
        value11(value11);
        value12(value12);
        value13(value13);
        value14(value14);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached SlfInvoicedtasksXdsdRecord
     */
    public SlfInvoicedtasksXdsdRecord() {
        super(SlfInvoicedtasksXdsd.SLF_INVOICEDTASKS_XDSD);
    }

    /**
     * Create a detached, initialised SlfInvoicedtasksXdsdRecord
     */
    public SlfInvoicedtasksXdsdRecord(Integer id, Integer invoiceid, String repoFullname, String username, String provider, String role, BigInteger value, String issueid, LocalDateTime assigned, LocalDateTime deadline, LocalDateTime invoiced, Integer estimationMinutes, BigInteger commission, Boolean ispullrequest) {
        super(SlfInvoicedtasksXdsd.SLF_INVOICEDTASKS_XDSD);

        set(0, id);
        set(1, invoiceid);
        set(2, repoFullname);
        set(3, username);
        set(4, provider);
        set(5, role);
        set(6, value);
        set(7, issueid);
        set(8, assigned);
        set(9, deadline);
        set(10, invoiced);
        set(11, estimationMinutes);
        set(12, commission);
        set(13, ispullrequest);
    }
}
