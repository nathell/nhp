data <- data.frame(
    v1 = c(18, 14, 18, 15, 43, 18, 43, 11, 180),
    v2 = c("Cost of acquire", "Internal reviewers / editors", "Copyright", "Editing", "Advertising and promotion", "Internal / administrative costs", "Technical costs (print, paper, etc.)", "Distribution and taxes", "Bookstore margin")
    )

data$v2 <- factor(data$v2, levels = data$v2)

pie(data$v1, labels = str_wrap(data$v2, width = 20), col = brewer.pal(5, "Set2"))
