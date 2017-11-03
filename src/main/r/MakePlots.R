
proteoformFrequencies <- read.csv("ReactomeAllProteoformsFrequencies.csv", sep = ",", header = T)
proteinFrequencies <- read.csv("ReactomeAllProteinFrequencies.csv", sep = ",", header = T)

modifiedProteinProteoformFrequencies <- read.csv("ReactomeModifiedProteinProteoformsFrequencies.csv", sep = ",", header = T)
modifiedProteinFrequencies <- read.csv("ReactomeModifiedProteinFrequencies.csv", sep = ",", header = T)

nonEmptyProteoformFrequencies <- read.csv("ReactomeNonEmptyProteoformsFrequencies.csv", sep = ",", header = T)
nonEmptyProteinFrequencies <- read.csv("ReactomeNonEmptyProteinsFrequencies.csv", sep = ",", header = T)

snpFrequencies <- read.csv("snpFrequencies.csv", sep = ";", header = F)

# All
frequencies <- rbind(proteoformFrequencies[,c(1,2,4,5)], proteinFrequencies)
frequencies <- rbind(modifiedProteinFrequencies[,c(1,2,4,5)], frequencies)
frequencies <- rbind(modifiedProteinProteoformFrequencies[,c(1,2,4,5)], frequencies)

# Just non empty proteins and their proteoforms
frequencies <- rbind(nonEmptyProteinFrequencies, nonEmptyProteoformFrequencies[,c(1,2,5,6)])

# Just the modified proteins and their proteoforms
frequencies <- rbind(modifiedProteinFrequencies[,c(1,2,4,5)], modifiedProteinProteoformFrequencies[,c(1,2,4,5)])

frequencies$Type <- as.character(frequencies$Type)
frequencies$Type <- factor(frequencies$Type, levels = c("Proteoform","Protein","Modified Protein Proteoform","Modified Protein"))
fillColors <- c( "#ffeda0","#fc9272", "#a1d99b","#a6bddb")
borderColors <- c("#feb24c","#de2d26","#31a354", "#2b8cbe")
library(ggplot2)

ggplot(frequencies, aes(Pathways, fill = Type, colour = Type)) +
  geom_density(alpha = 0.8, adjust = 3) + ylim(0, 0.4) + xlim(0, 20) + theme_bw() + scale_fill_manual(values = borderColors) + scale_color_manual(values= borderColors)


ggplot(frequencies, aes(Reactions, fill = Type, colour = Type)) +
  geom_density(alpha = 0.8, adjust = 1.5) + ylim(0, 0.4) + xlim(0, 10) + theme_bw() + scale_fill_manual(values = borderColors) + scale_color_manual(values= borderColors)

g <- ggplot(frequencies, aes(Pathways)) + scale_fill_brewer(palette = "Spectral")
g + geom_histogram(aes(fill=Type), 
                   binwidth = 1, 
                   col="black", 
                   size=.1, 
                   position = "dodge"
                   ) + xlim(0, 20) + 
theme_bw() + 
scale_fill_manual(values=borderColors) +
scale_fill_manual(values=borderColors) + 
theme(plot.background = element_rect(fill = "#f2edea"), 
      panel.background = element_rect(fill = "#f2edea", colour = "darkgrey"), 
      legend.background = element_rect(fill = "#f2edea", colour = "#f2edea"))

g <- ggplot(frequencies, aes(Reactions)) + scale_fill_brewer(palette = "Spectral")
g + geom_histogram(aes(fill=Type), 
                   binwidth = 1, 
                   col="black", 
                   size=.1, 
                   position = "dodge") + xlim(0, 30)+
  theme_bw() + 
  scale_fill_manual(values=borderColors) +
  scale_fill_manual(values=borderColors) + 
  theme(plot.background = element_rect(fill = "#f2edea"), 
        panel.background = element_rect(fill = "#f2edea", colour = "darkgrey"), 
        legend.position="none")

p <- ggplot(frequencies, aes(Type, Pathways))
p + geom_violin(aes(fill = Type), adjust = 1) + ylim(0, 20) + theme_bw() + scale_fill_manual(values=borderColors) + theme(plot.background = element_rect(fill = "#f2edea"), panel.background = element_rect(fill = "#f2edea", colour = "darkgrey"), legend.background = element_rect(fill = "#f2edea", colour = "#f2edea"))


p <- ggplot(frequencies, aes(Type, Reactions))
p + geom_violin(aes(fill = Type), adjust = 0.7) + ylim(0, 30) + 
theme_bw() + 
scale_fill_manual(values=borderColors) +
scale_fill_manual(values=borderColors) + 
theme(plot.background = element_rect(fill = "#f2edea"), 
      panel.background = element_rect(fill = "#f2edea", colour = "darkgrey"), 
      legend.background = element_rect(fill = "#f2edea", colour = "#f2edea"))

#############################################################################
  
numberProteoformsPerProteins <- read.csv("NumberProteoformsPerProtein.csv", sep = ",", header = T)

ggplot(numberProteoformsPerProteins, aes(proteoforms)) +
  geom_density(alpha = 0.1, adjust = 2) + ylim(0, 1.5) + xlim(0, 10) + ggtitle("Number of proteoforms per protein")

g <- ggplot(numberProteoformsPerProteins, aes(proteoforms, position = "dodge"))
g + geom_histogram(binwidth = .3, 
                   col="blue", 
                   size=.1) +  # change binwidth
  labs(title="Number of proteoforms per protein") + xlim(0, 10) + theme_bw()

############################################################################
# Ratios of proteoform matching / protein matching

names(nonEmptyProteoformFrequencies)[names(nonEmptyProteoformFrequencies)=="Pathways"] <- "PathwaysProteoforms"
names(nonEmptyProteoformFrequencies)[names(nonEmptyProteoformFrequencies)=="Reactions"] <- "reactionsProteoforms"

mergedDf <- merge(nonEmptyProteinFrequencies,nonEmptyProteoformFrequencies,by="protein")
num <- seq(8, 9)
den <- seq(3, 4)

ratiosPathways <- mergedDf[,8] / mergedDf[,3]
ratiosDf <- data.frame(mergedDf[,c(1)], ratiosPathways, rep("Pathways",nrow(mergedDf)))
colnames(ratiosDf) <- c("Protein", "Ratio", "Type")

ratiosReactions <- mergedDf[,9] / mergedDf[,4]
tmp <- data.frame(mergedDf[,c(1)], ratiosReactions, rep("Reactions",nrow(mergedDf)))
colnames(tmp) <- c("Protein", "Ratio", "Type")
ratiosDf <- rbind(ratiosDf, tmp)

median.quartile <- function(x){
  out <- quantile(x, probs = c(0.25,0.5,0.75))
  names(out) <- c("ymin","y","ymax")
  return(out) 
}

g <- ggplot(ratiosDf, aes(Ratio, fill = Type, colour = Type))
g + geom_density(alpha = 0.4, adjust = 0.1) + ggtitle("Ratio entities mapped with Proteoform / Protein")+ theme_bw()+ scale_fill_manual(values = fillColors) + scale_color_manual(values= borderColors)

p <- ggplot(ratiosDf, aes(Type, Ratio))
p + geom_violin(aes(fill = Type), adjust = 1) + ylim(0, 1) + theme_bw() + scale_fill_manual(values=fillColors)+
  stat_summary(fun.y=median.quartile,geom='point')
