# Gelp

Gelp (Google + Yelp - I'm not very creative...) is a matching engine which can be used to link the Yelp and Google entries for a given business.

In its current iteration, the engine takes in four coordinate points defining a geographic region and outputs a mapping between Yelp businesses and their corresponding entry in Google Places within that region. For example, it finds and matches this:

![yelp-result](https://cloud.githubusercontent.com/assets/3643059/7053613/64de63d6-ddeb-11e4-93c5-0b08d25f951f.png)

with this:

![google-result](https://cloud.githubusercontent.com/assets/3643059/7053615/6697ec1a-ddeb-11e4-9b50-10682e8fa4e9.png)

# Why

This started as an innocent research project. I wanted to know if there were any worthwhile inferences that could be drawn from analyzing the data on the two platforms. For example, which platform generally has more reviews? How big is the margin? Does one platform skew more negative than the other? Does one know about more places than the other? etc.

To do this sort of research, you first need to establish a link between a business on one platform and its corresponding entry on the other. As it turns out, that isn't a trivial task - thus this project.

My own curiosity aside, there are other potential uses for an engine like this. One could imagine curating better data on a given merchant by incorporating the data from each platform, or perhaps building a discovery or search tool that simply brings up the paired results from both platforms. I personally plan to leverage the tool to port all of my Google Places reviews over to Yelp, because I can. ¯\\\_(ツ)_/¯

# How

Here's a quick overview of how the engine works:

1. Creates a mesh of coordinates that blankets the desired search region.
2. For each coordinate, fetches the corresponding Yelp/Google search results.
3. Temporarily stores the results.
4. Runs a matching algorithm based on the defining characteristics of a business, such as phone number or name.
5. If the match confidence is sufficiently high, records the link between the entries on each platform.

# Usage

The project is not in a very user friendly state yet. I'm hoping to address that soon - time permitting - but in the meantime feel free to contact me and I will help you through the setup process.
