public class test {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Type cannot be null.")
    @Enumerated(EnumType.STRING)
    private Type type;

    @NotNull(message = "Category cannot be null.")
    @ManyToOne
    private Category category;

    @NotNull(message = "Cost cannot be null.")
    @DecimalMin(value = "0.01", inclusive = true, message = "Cost must be greater than zero.")
    @DecimalMax(value = "1000000000.00", inclusive = true, message = "Cost cannot exceed 1 billion.")
    private BigDecimal cost;

    @NotNull(message = "DateTime cannot be null.")
    private LocalDateTime dateTime;

    @NotNull(message = "Description cannot be null.")
    @Size(max = 1000, message = "Description cannot exceed 1000 characters.")
    private String description;
}
