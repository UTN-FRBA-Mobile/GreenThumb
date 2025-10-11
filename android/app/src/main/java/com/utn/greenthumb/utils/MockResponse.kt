package com.utn.greenthumb.utils

import com.utn.greenthumb.data.model.plantid.*

object MockResponse {
    fun mockIdentificationResponse(): IdentificationResponse {
        return IdentificationResponse(
            accessToken = "U9aYoIa7iqF0IA9",
            result = ResultDTO(
                classification = SuggestionsDTO(
                    suggestions = listOf(
                        SuggestionDTO(
                            id = "662e0f8d4202acfc",
                            name = "Rhaphiolepis bibas",
                            probability = 0.8843,
                            similarImages = listOf(
                                SimilarImageDTO(
                                    url = "https://plant-id.ams3.cdn.digitaloceanspaces.com/similar_images/5/b32/0549ca7acfc14f40a325e8de90ad8b9fdbcff.jpeg",
                                    similarity = 0.889
                                ),
                                SimilarImageDTO(
                                    url = "https://plant-id.ams3.cdn.digitaloceanspaces.com/similar_images/5/e63/ee1c405b78fa21f5107cf63a064ac1e1ae01a.jpeg",
                                    similarity = 0.789
                                ),
                                SimilarImageDTO(
                                    url = "https://plant.id/media/imgs/21d84ed3ead945d4a6ad28eb8b0ea562.jpg",
                                    similarity = 0.989
                                )
                            ),
                            details = DetailsDTO(
                                commonNames = listOf("níspero japonés", "nisperero del Japón", "níspero"),
                                taxonomy = TaxonomyDTO(
                                    taxonomyClass = "Magnoliopsida",
                                    genus = "Rhaphiolepis",
                                    order = "Rosales",
                                    family = "Rosaceae",
                                    phylum = "Tracheophyta",
                                    kingdom = "Plantae"
                                ),
                                url = "https://es.wikipedia.org/wiki/Eriobotrya_japonica",
                                rank = "species",
                                description = DescriptionDTO(
                                    value = "Eriobotrya japonica, comúnmente llamado níspero japonés,\u200B nisperero del Japón\u200B o simplemente níspero, es un árbol frutal perenne de la familia Rosaceae,\u200B originario del sudeste de China,\u200B donde se conoce como pípá, 枇杷.\u200B Fue introducido en Japón, donde se naturalizó y donde lleva cultivándose más de mil años. También se naturalizó en la India, la cuenca mediterránea, Canarias, Pakistán, Chile, Argentina , Ecuador,Costa Rica y muchas otras áreas. Se cree que la inmigración china llevó el níspero a Hawái.\\nSe menciona a menudo en la antigua literatura china, por ejemplo en los poemas de Li Bai, y en la literatura portuguesa se conoce desde la era de los descubrimientos.\\nEn noviembre se celebra el Festival del Níspero en San Juan del Obispo, Guatemala.\\nEl fruto de esta especie ha ido sustituyendo al del níspero europeo (Mespilus germanica), de forma que, en la actualidad, al hablar de «níspero» se sobreentiende que se está haciendo referencia al japonés.",
                                    citation = "https://es.wikipedia.org/wiki/Eriobotrya_japonica"
                                ),
                                synonyms = listOf(
                                    "Crataegus bibas",
                                    "Eriobotrya fragrans",
                                    "Eriobotrya fragrans var. furfuracea",
                                    "Eriobotrya japonica",
                                    "Eriobotrya japonica f. variegata",
                                    "Mespilus japonica",
                                    "Photinia japonica",
                                    "Pyrus bibas",
                                    "Pyrus williamtelliana",
                                    "Rhaphiolepis loquata",
                                    "Rhaphiolepis williamtelliana",
                                    "Rhaphiolepis williamtelliana var. furfuracea"),
                                watering = WateringDTO(max = 2, min = 2)
                            )
                        ),
                        SuggestionDTO(
                            id = "f57c6c7e33d57d76",
                            name = "Pouteria",
                            probability = 0.3279,
                            similarImages = listOf(
                                SimilarImageDTO(
                                    url = "https://plant-id.ams3.cdn.digitaloceanspaces.com/similar_images/5/c91/39bab662ad4edd214040d4a001785e1f544d8.jpeg",
                                    similarity = 0.691
                                )
                            ),
                            details = DetailsDTO(
                                commonNames = listOf("Zapotillos"),
                                taxonomy = TaxonomyDTO(
                                    taxonomyClass = "Magnoliopsida",
                                    genus = "Pouteria",
                                    order = "Ericales",
                                    family = "Sapotaceae",
                                    phylum = "Tracheophyta",
                                    kingdom = "Plantae"
                                ),
                                url = "https://es.wikipedia.org/wiki/Pouteria",
                                rank = "genus",
                                description = DescriptionDTO(
                                    value = "Pouteria es un género de la familia Sapotaceae. Todos sus miembros son árboles. Muchas especies producen frutas comestibles. Algunas incluso son comercialmente recolectadas y vendidas en mercados locales o enlatadas para su venta. En la región del Río de la Plata reciben el nombre común de aguaí \u200B o aguay.",
                                    citation = "https://es.wikipedia.org/wiki/Pouteria"
                                ),
                                synonyms = listOf(
                                    "Achradelpha",
                                    "Aningeria",
                                    "Aningueria",
                                    "Barylucuma",
                                    "Beccarimnea",
                                    "Beccarimnia",
                                    "Blabea",
                                    "Caleatia",
                                    "Calocarpum",
                                    "Calospermum",
                                    "Caramuri",
                                    "Chaetocarpus",
                                    "Daphniluma",
                                    "Discoluma",
                                    "Dithecoluma",
                                    "Eglerodendron",
                                    "Englerella",
                                    "Eremoluma",
                                    "Franchetella",
                                    "Gayella",
                                    "Gomphiluma",
                                    "Guapeba",
                                    "Guapebeira",
                                    "Ichthyophora",
                                    "Krugella",
                                    "Labatia",
                                    "Leioluma",
                                    "Lucuma",
                                    "Maesoluma",
                                    "Malacantha",
                                    "Microluma",
                                    "Myrsiniluma",
                                    "Myrtiluma",
                                    "Nemaluma",
                                    "Neolabatia",
                                    "Neoxythece",
                                    "Ochroluma",
                                    "Oxythece",
                                    "Paralabatia",
                                    "Peteniodendron",
                                    "Piresodendron",
                                    "Pleioluma",
                                    "Podoluma",
                                    "Prozetia",
                                    "Pseudocladia",
                                    "Pseudolabatia",
                                    "Pseudoxythece",
                                    "Radlkoferella",
                                    "Richardella",
                                    "Sandwithiodoxa",
                                    "Siderocarpus",
                                    "Syzygiopsis",
                                    "Urbanella",
                                    "Van-Royena",
                                    "Woikoia"
                                ),
                                watering = null
                            )
                        ),
                        SuggestionDTO(
                            id = "9b781ff8be4b5e07",
                            name = "Ficus luschnathiana",
                            probability = 0.0182,
                            similarImages = listOf(
                                SimilarImageDTO(
                                    url = "https://plant-id.ams3.cdn.digitaloceanspaces.com/similar_images/5/766/a49b5f3dd563bd75e7fd77dbb7b93c6c9adcf.jpeg",
                                    similarity = 0.554
                                )
                            ),
                            details = DetailsDTO(
                                commonNames = listOf("Ibá poí", "Higuerón"),
                                taxonomy = TaxonomyDTO(
                                    taxonomyClass = "Magnoliopsida",
                                    genus = "Ficus",
                                    order = "Rosales",
                                    family = "Moraceae",
                                    phylum = "Tracheophyta",
                                    kingdom = "Plantae"
                                ),
                                url = "https://es.wikipedia.org/wiki/Ficus_luschnathiana",
                                rank = "species",
                                description = DescriptionDTO(
                                    value = "Ficus luschnathiana, comúnmente llamado higuerón o yvapoí o guapoí (del guaraní yva-po'y), es una especie de planta epifita de la familia Moraceae. Es endémica de Brasil, Argentina, Paraguay, Uruguay y Bolivia.",
                                    citation = "https://es.wikipedia.org/wiki/Ficus_luschnathiana"
                                ),
                                synonyms = listOf(
                                    "Ficus diabolica",
                                    "Ficus diabolica laurina",
                                    "Ficus diabolica maior",
                                    "Ficus diabolica minor",
                                    "Ficus erubescens",
                                    "Ficus horquetensis",
                                    "Ficus ibapohi",
                                    "Ficus ibapophy",
                                    "Ficus luschnathiana (Miq.) Miq., 1867",
                                    "Ficus monckii",
                                    "Ficus monckii subcuneata",
                                    "Ficus speciosa",
                                    "Urostigma luschnathianum"
                                ),
                                watering = null
                            )
                        )
                    )

                ),
                isPlant = IsPlantDTO(
                    probability = 0.96186167,
                    threshold = 0.5,
                    binary = true
                )
            )
        )
    }
}
