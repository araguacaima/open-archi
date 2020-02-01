/***** ELEMENT SHAPES *****/

/* ARCHITECTURE_MODEL */

INSERT INTO diagrams.colorscheme (id,
                                  name,
                                  fillcolor,
                                  strokecolor,
                                  textcolor)
VALUES ('fa1516dc-c224-4967-9def-97237b019121',
        'PRIMARY',
        'DarkSlateGray',
        '#EAEDED',
        '#EAEDED');

INSERT INTO diagrams.colorscheme (id,
                                  name,
                                  fillcolor,
                                  strokecolor,
                                  textcolor)
VALUES ('b4f2850a-886f-4d9b-9d92-1a8933810983',
        'SECONDARY',
        '#EAEDED',
        'DarkSlateGray',
        'DarkSlateGray');

INSERT INTO diagrams.elementshape (id,
                                   type,
                                   input,
                                   output,
                                   figure,
                                   isGroup)
VALUES ('061d46ad-7c6f-477c-a16f-3900dcacce57',
        'ARCHITECTURE_MODEL',
        TRUE,
        TRUE,
        '',
        TRUE);

INSERT INTO diagrams.element_shape_color_schemes (element_shape_id,
                                                  color_scheme_id)
VALUES ('061d46ad-7c6f-477c-a16f-3900dcacce57',
        'fa1516dc-c224-4967-9def-97237b019121');

INSERT INTO diagrams.element_shape_color_schemes (element_shape_id,
                                                  color_scheme_id)
VALUES ('061d46ad-7c6f-477c-a16f-3900dcacce57',
        'b4f2850a-886f-4d9b-9d92-1a8933810983');

/* LAYER */

INSERT INTO diagrams.colorscheme (id,
                                  name,
                                  fillcolor,
                                  strokecolor,
                                  textcolor)
VALUES ('1b855bc8-ea5b-4d76-946e-3ae080dcaac0',
        'PRIMARY',
        'LimeGreen',
        '#eafaea',
        '#eafaea');

INSERT INTO diagrams.colorscheme (id,
                                  name,
                                  fillcolor,
                                  strokecolor,
                                  textcolor)
VALUES ('ba3d7b64-7993-4cfd-adbb-8a512785dab1',
        'SECONDARY',
        '#eafaea',
        'LimeGreen',
        'LimeGreen');

INSERT INTO diagrams.elementshape (id,
                                   type,
                                   input,
                                   output,
                                   figure,
                                   isGroup)
VALUES ('713af8da-0dca-4419-9cfd-b842438392d8',
        'LAYER',
        TRUE,
        TRUE,
        '',
        TRUE);

INSERT INTO diagrams.element_shape_color_schemes (element_shape_id,
                                                  color_scheme_id)
VALUES ('713af8da-0dca-4419-9cfd-b842438392d8',
        '1b855bc8-ea5b-4d76-946e-3ae080dcaac0');

INSERT INTO diagrams.element_shape_color_schemes (element_shape_id,
                                                  color_scheme_id)
VALUES ('713af8da-0dca-4419-9cfd-b842438392d8',
        'ba3d7b64-7993-4cfd-adbb-8a512785dab1');

/* SYSTEM */

INSERT INTO diagrams.colorscheme (id,
                                  name,
                                  fillcolor,
                                  strokecolor,
                                  textcolor)
VALUES ('1fb4df53-05f3-4725-83d3-f51e3833c428',
        'PRIMARY',
        '#02172C',
        '#e5e7e9',
        '#e5e7e9');

INSERT INTO diagrams.colorscheme (id,
                                  name,
                                  fillcolor,
                                  strokecolor,
                                  textcolor)
VALUES ('f7ea3b56-e630-42d1-9e9f-e901e0d959c1',
        'SECONDARY',
        '#e5e7e9',
        '#02172C',
        '#02172C');

INSERT INTO diagrams.elementshape (id,
                                   type,
                                   input,
                                   output,
                                   figure,
                                   isGroup)
VALUES ('9019d959-8ffe-4615-a9bb-f46588d7406d',
        'SYSTEM',
        TRUE,
        TRUE,
        '',
        TRUE);

INSERT INTO diagrams.element_shape_color_schemes (element_shape_id,
                                                  color_scheme_id)
VALUES ('9019d959-8ffe-4615-a9bb-f46588d7406d',
        '1fb4df53-05f3-4725-83d3-f51e3833c428');

INSERT INTO diagrams.element_shape_color_schemes (element_shape_id,
                                                  color_scheme_id)
VALUES ('9019d959-8ffe-4615-a9bb-f46588d7406d',
        'f7ea3b56-e630-42d1-9e9f-e901e0d959c1');

/* CONTAINER */

INSERT INTO diagrams.colorscheme (id,
                                  name,
                                  fillcolor,
                                  strokecolor,
                                  textcolor)
VALUES ('018b2e0f-f05b-4db5-a04a-aa34e8fecc56',
        'PRIMARY',
        '#08427B',
        '#e6ecf1',
        '#e6ecf1');

INSERT INTO diagrams.colorscheme (id,
                                  name,
                                  fillcolor,
                                  strokecolor,
                                  textcolor)
VALUES ('a540ccfb-2d24-4b8f-993d-f97a98e37472',
        'SECONDARY',
        '#e6ecf1',
        '#08427B',
        '#08427B');

INSERT INTO diagrams.elementshape (id,
                                   type,
                                   input,
                                   output,
                                   figure,
                                   isGroup)
VALUES ('6222894c-d307-49dd-97cf-bf95938bc9b8',
        'CONTAINER',
        TRUE,
        TRUE,
        '',
        TRUE);

INSERT INTO diagrams.element_shape_color_schemes (element_shape_id,
                                                  color_scheme_id)
VALUES ('6222894c-d307-49dd-97cf-bf95938bc9b8',
        '018b2e0f-f05b-4db5-a04a-aa34e8fecc56');

INSERT INTO diagrams.element_shape_color_schemes (element_shape_id,
                                                  color_scheme_id)
VALUES ('6222894c-d307-49dd-97cf-bf95938bc9b8',
        'a540ccfb-2d24-4b8f-993d-f97a98e37472');

/* COMPONENT */

INSERT INTO diagrams.colorscheme (id,
                                  name,
                                  fillcolor,
                                  strokecolor,
                                  textcolor)
VALUES ('f355afea-1c1c-4986-8ef1-16b4b8e250af', 'PRIMARY', '#1368BD', '#e7eff8', '#e7eff8');

INSERT INTO diagrams.colorscheme (id,
                                  name,
                                  fillcolor,
                                  strokecolor,
                                  textcolor)
VALUES ('4209c888-3cf1-44e2-b72b-3d6ad7c6b8e2', 'SECONDARY', '#e7eff8', '#1368BD', '#1368BD');

INSERT INTO diagrams.elementshape (id,
                                   type,
                                   input,
                                   output,
                                   figure,
                                   isGroup)
VALUES ('c92ce01e-9c32-4aa5-ada7-a8edc0c546fd',
        'COMPONENT',
        TRUE,
        TRUE,
        '',
        FALSE);

INSERT INTO diagrams.element_shape_color_schemes (element_shape_id,
                                                  color_scheme_id)
VALUES ('c92ce01e-9c32-4aa5-ada7-a8edc0c546fd',
        'f355afea-1c1c-4986-8ef1-16b4b8e250af');

INSERT INTO diagrams.element_shape_color_schemes (element_shape_id,
                                                  color_scheme_id)
VALUES ('c92ce01e-9c32-4aa5-ada7-a8edc0c546fd',
        '4209c888-3cf1-44e2-b72b-3d6ad7c6b8e2');

/* COMPONENT_MODEL */

INSERT INTO diagrams.colorscheme (id,
                                  name,
                                  fillcolor,
                                  strokecolor,
                                  textcolor)
VALUES ('463cfa95-b1ed-418e-a2a0-c67ac19865c7',
        'PRIMARY',
        'DarkSlateGray',
        '#EAEDED',
        '#EAEDED');

INSERT INTO diagrams.colorscheme (id,
                                  name,
                                  fillcolor,
                                  strokecolor,
                                  textcolor)
VALUES ('fcd5c6d3-34ca-4deb-834f-58800a16af6a',
        'SECONDARY',
        '#EAEDED',
        'DarkSlateGray',
        'DarkSlateGray');

INSERT INTO diagrams.elementshape (id,
                                   type,
                                   input,
                                   output,
                                   figure,
                                   isGroup)
VALUES ('148133ad-a3b4-49d1-92e0-6516e9ba2b04',
        'COMPONENT_MODEL',
        TRUE,
        TRUE,
        '',
        TRUE);

INSERT INTO diagrams.element_shape_color_schemes (element_shape_id,
                                                  color_scheme_id)
VALUES ('148133ad-a3b4-49d1-92e0-6516e9ba2b04',
        '463cfa95-b1ed-418e-a2a0-c67ac19865c7');

INSERT INTO diagrams.element_shape_color_schemes (element_shape_id,
                                                  color_scheme_id)
VALUES ('148133ad-a3b4-49d1-92e0-6516e9ba2b04',
        'fcd5c6d3-34ca-4deb-834f-58800a16af6a');


/* GROUP */

INSERT INTO diagrams.colorscheme (id,
                                  name,
                                  fillcolor,
                                  strokecolor,
                                  textcolor)
VALUES ('c5982eb9-edb0-4865-8f83-92cf35782fff',
        'PRIMARY',
        '#08427B',
        '#e6ecf1',
        '#e6ecf1');

INSERT INTO diagrams.colorscheme (id,
                                  name,
                                  fillcolor,
                                  strokecolor,
                                  textcolor)
VALUES ('6373b1d8-b146-450a-9805-b3e41e2048e7',
        'SECONDARY',
        '#e6ecf1',
        '#08427B',
        '#08427B');

INSERT INTO diagrams.elementshape (id,
                                   type,
                                   input,
                                   output,
                                   figure,
                                   isGroup)
VALUES ('1480c08c-8c1f-4852-820a-9136e6dc83f0',
        'GROUP',
        TRUE,
        TRUE,
        '',
        TRUE);

INSERT INTO diagrams.element_shape_color_schemes (element_shape_id,
                                                  color_scheme_id)
VALUES ('1480c08c-8c1f-4852-820a-9136e6dc83f0',
        'c5982eb9-edb0-4865-8f83-92cf35782fff');

INSERT INTO diagrams.element_shape_color_schemes (element_shape_id,
                                                  color_scheme_id)
VALUES ('1480c08c-8c1f-4852-820a-9136e6dc83f0',
        '6373b1d8-b146-450a-9805-b3e41e2048e7');

/* ELEMENT */

INSERT INTO diagrams.colorscheme (id,
                                  name,
                                  fillcolor,
                                  strokecolor,
                                  textcolor)
VALUES ('902837a5-aa3d-48d9-8427-0351535730e3', 'PRIMARY', '#1368BD', '#e7eff8', '#e7eff8');

INSERT INTO diagrams.colorscheme (id,
                                  name,
                                  fillcolor,
                                  strokecolor,
                                  textcolor)
VALUES ('6be091db-e825-4b3e-8d51-348892c16a24', 'SECONDARY', '#e7eff8', '#1368BD', '#1368BD');

INSERT INTO diagrams.elementshape (id,
                                   type,
                                   input,
                                   output,
                                   figure,
                                   isGroup)
VALUES ('e0de5a97-e70f-4b65-ad2d-de2c7335997d',
        'ELEMENT',
        TRUE,
        TRUE,
        '',
        FALSE);

INSERT INTO diagrams.element_shape_color_schemes (element_shape_id,
                                                  color_scheme_id)
VALUES ('e0de5a97-e70f-4b65-ad2d-de2c7335997d',
        '902837a5-aa3d-48d9-8427-0351535730e3');

INSERT INTO diagrams.element_shape_color_schemes (element_shape_id,
                                                  color_scheme_id)
VALUES ('e0de5a97-e70f-4b65-ad2d-de2c7335997d',
        '6be091db-e825-4b3e-8d51-348892c16a24');


/***** DIAGRAM TYPES *****/

/* ARCHITECTURE_MODEL */

INSERT INTO diagrams.colorscheme (id,
                                  name,
                                  fillcolor,
                                  strokecolor,
                                  textcolor)
VALUES ('a8124da6-0474-4f94-9218-4da3deb6a84a',
        'PRIMARY',
        '#009999',
        '#FF7400',
        '#FFFFFF');

INSERT INTO diagrams.colorscheme (id,
                                  name,
                                  fillcolor,
                                  strokecolor,
                                  textcolor)
VALUES ('eace0c77-cd49-40fe-af81-8d483cfe7c7d',
        'SECONDARY',
        '#FF7400',
        '#009999',
        '#FFFFFF');

INSERT INTO diagrams.diagramtype (id,
                                  type,
                                  figure,
                                  enabled)
VALUES ('6edaf233-d06b-4815-98d0-5dd9f9421103',
        'ARCHITECTURE_MODEL',
        '',
        TRUE);

INSERT INTO diagrams.diagram_type_color_schemes (diagram_type_id,
                                                 color_scheme_id)
VALUES ('6edaf233-d06b-4815-98d0-5dd9f9421103',
        'a8124da6-0474-4f94-9218-4da3deb6a84a');

INSERT INTO diagrams.diagram_type_color_schemes (diagram_type_id,
                                                 color_scheme_id)
VALUES ('6edaf233-d06b-4815-98d0-5dd9f9421103',
        'eace0c77-cd49-40fe-af81-8d483cfe7c7d');

INSERT INTO diagrams.diagram_type_element_shapes (diagram_type_id,
                                                  element_shape_Id)
VALUES ('6edaf233-d06b-4815-98d0-5dd9f9421103',
        '713af8da-0dca-4419-9cfd-b842438392d8');

INSERT INTO diagrams.diagram_type_element_shapes (diagram_type_id,
                                                  element_shape_Id)
VALUES ('6edaf233-d06b-4815-98d0-5dd9f9421103',
        '9019d959-8ffe-4615-a9bb-f46588d7406d');

INSERT INTO diagrams.diagram_type_element_shapes (diagram_type_id,
                                                  element_shape_Id)
VALUES ('6edaf233-d06b-4815-98d0-5dd9f9421103',
        '6222894c-d307-49dd-97cf-bf95938bc9b8');

INSERT INTO diagrams.diagram_type_element_shapes (diagram_type_id,
                                                  element_shape_Id)
VALUES ('6edaf233-d06b-4815-98d0-5dd9f9421103',
        'c92ce01e-9c32-4aa5-ada7-a8edc0c546fd');

/* COMPONENT_MODEL */

INSERT INTO diagrams.colorscheme (id,
                                  name,
                                  fillcolor,
                                  strokecolor,
                                  textcolor)
VALUES ('d73d9951-9e4f-4b11-a96e-d5ada92a5df6',
        'PRIMARY',
        '#FF8E00',
        '#0A67A3',
        '#0A67A3');

INSERT INTO diagrams.colorscheme (id,
                                  name,
                                  fillcolor,
                                  strokecolor,
                                  textcolor)
VALUES ('55be05e6-ee2c-4bbd-9a6d-91ad4d0c69f1',
        'SECONDARY',
        '#0A67A3',
        '#FF8E00',
        '#FF8E00');

INSERT INTO diagrams.diagramtype (id,
                                  type,
                                  figure,
                                  enabled)
VALUES ('1e990f6f-2a41-4744-bcfa-35fa7a59db1f',
        'COMPONENT_MODEL',
        '',
        TRUE);

INSERT INTO diagrams.diagram_type_color_schemes (diagram_type_id,
                                                 color_scheme_id)
VALUES ('1e990f6f-2a41-4744-bcfa-35fa7a59db1f',
        'd73d9951-9e4f-4b11-a96e-d5ada92a5df6');

INSERT INTO diagrams.diagram_type_color_schemes (diagram_type_id,
                                                 color_scheme_id)
VALUES ('1e990f6f-2a41-4744-bcfa-35fa7a59db1f',
        '55be05e6-ee2c-4bbd-9a6d-91ad4d0c69f1');

INSERT INTO diagrams.diagram_type_element_shapes (diagram_type_id,
                                                  element_shape_Id)
VALUES ('1e990f6f-2a41-4744-bcfa-35fa7a59db1f',
        '1480c08c-8c1f-4852-820a-9136e6dc83f0');

INSERT INTO diagrams.diagram_type_element_shapes (diagram_type_id,
                                                  element_shape_Id)
VALUES ('1e990f6f-2a41-4744-bcfa-35fa7a59db1f',
        'e0de5a97-e70f-4b65-ad2d-de2c7335997d');

INSERT INTO diagrams.diagram_type_element_shapes (diagram_type_id,
                                                  element_shape_Id)
VALUES ('1e990f6f-2a41-4744-bcfa-35fa7a59db1f',
        '713af8da-0dca-4419-9cfd-b842438392d8');

/* FLOWCHART_MODEL */

INSERT INTO diagrams.colorscheme (id,
                                  name,
                                  fillcolor,
                                  strokecolor,
                                  textcolor)
VALUES ('4c5fe9ba-91ba-4ddb-b2fa-3fc911464e69',
        'PRIMARY',
        '#E20048',
        '#FF4500',
        '#FF4500');

INSERT INTO diagrams.colorscheme (id,
                                  name,
                                  fillcolor,
                                  strokecolor,
                                  textcolor)
VALUES ('cacf9a51-4e85-42ed-9af8-8ff665d366b7',
        'SECONDARY',
        '#FF4500',
        '#E20048',
        '#E20048');

INSERT INTO diagrams.diagramtype (id,
                                  type,
                                  figure,
                                  enabled)
VALUES ('ac0208c8-bda5-4593-87d6-593d7fde6386',
        'FLOWCHART_MODEL',
        '',
        FALSE);

INSERT INTO diagrams.diagram_type_color_schemes (diagram_type_id,
                                                 color_scheme_id)
VALUES ('ac0208c8-bda5-4593-87d6-593d7fde6386',
        '4c5fe9ba-91ba-4ddb-b2fa-3fc911464e69');

INSERT INTO diagrams.diagram_type_color_schemes (diagram_type_id,
                                                 color_scheme_id)
VALUES ('ac0208c8-bda5-4593-87d6-593d7fde6386',
        'cacf9a51-4e85-42ed-9af8-8ff665d366b7');

/* SEQUENCE_MODEL */

INSERT INTO diagrams.colorscheme (id,
                                  name,
                                  fillcolor,
                                  strokecolor,
                                  textcolor)
VALUES ('4cfbf9df-71d0-4470-913d-ff65e949047e',
        'PRIMARY',
        '#FFDB00',
        '#4312AE',
        '#4312AE');

INSERT INTO diagrams.colorscheme (id,
                                  name,
                                  fillcolor,
                                  strokecolor,
                                  textcolor)
VALUES ('3628c349-2728-4543-a5ca-9957054db08f',
        'SECONDARY',
        '#4312AE',
        '#FFDB00',
        '#FFDB00');

INSERT INTO diagrams.diagramtype (id,
                                  type,
                                  figure,
                                  enabled)
VALUES ('9b37934c-bbfc-4050-85f8-8cf1f12fd4ef',
        'SEQUENCE_MODEL',
        '',
        FALSE);

INSERT INTO diagrams.diagram_type_color_schemes (diagram_type_id,
                                                 color_scheme_id)
VALUES ('9b37934c-bbfc-4050-85f8-8cf1f12fd4ef',
        '4cfbf9df-71d0-4470-913d-ff65e949047e');

INSERT INTO diagrams.diagram_type_color_schemes (diagram_type_id,
                                                 color_scheme_id)
VALUES ('9b37934c-bbfc-4050-85f8-8cf1f12fd4ef',
        '3628c349-2728-4543-a5ca-9957054db08f');

/* GANTT_MODEL */

INSERT INTO diagrams.colorscheme (id,
                                  name,
                                  fillcolor,
                                  strokecolor,
                                  textcolor)
VALUES ('f762aa62-6101-4f1c-80af-a3a8d0e96428',
        'PRIMARY',
        '#8BEA00',
        '#D60062',
        '#D60062');

INSERT INTO diagrams.colorscheme (id,
                                  name,
                                  fillcolor,
                                  strokecolor,
                                  textcolor)
VALUES ('4f8454f8-7b27-464f-a434-7a8b3a0c41a6',
        'SECONDARY',
        '#D60062',
        '#8BEA00',
        '#8BEA00');

INSERT INTO diagrams.diagramtype (id,
                                  type,
                                  figure,
                                  enabled)
VALUES ('c58eb3b9-9559-4987-b51f-5d2d56e0bc66',
        'GANTT_MODEL',
        '',
        FALSE);

INSERT INTO diagrams.diagram_type_color_schemes (diagram_type_id,
                                                 color_scheme_id)
VALUES ('c58eb3b9-9559-4987-b51f-5d2d56e0bc66',
        'f762aa62-6101-4f1c-80af-a3a8d0e96428');

INSERT INTO diagrams.diagram_type_color_schemes (diagram_type_id,
                                                 color_scheme_id)
VALUES ('c58eb3b9-9559-4987-b51f-5d2d56e0bc66',
        '4f8454f8-7b27-464f-a434-7a8b3a0c41a6');

/* ENTITY_RELATIONSHIP_MODEL */

INSERT INTO diagrams.colorscheme (id,
                                  name,
                                  fillcolor,
                                  strokecolor,
                                  textcolor)
VALUES ('0d172481-e216-4abb-bdaf-d13fc9a92a5f',
        'PRIMARY',
        '#0A64A4',
        '#FF9000',
        '#FF9000');

INSERT INTO diagrams.colorscheme (id,
                                  name,
                                  fillcolor,
                                  strokecolor,
                                  textcolor)
VALUES ('57ebfe49-b3fa-43a9-a914-03510fb1e7ba',
        'SECONDARY',
        '#FF9000',
        '#0A64A4',
        '#0A64A4');

INSERT INTO diagrams.diagramtype (id,
                                  type,
                                  figure,
                                  enabled)
VALUES ('62432eba-34bd-4a5e-904b-64a06068a50d',
        'ENTITY_RELATIONSHIP_MODEL',
        '',
        FALSE);

INSERT INTO diagrams.diagram_type_color_schemes (diagram_type_id,
                                                 color_scheme_id)
VALUES ('62432eba-34bd-4a5e-904b-64a06068a50d',
        '0d172481-e216-4abb-bdaf-d13fc9a92a5f');

INSERT INTO diagrams.diagram_type_color_schemes (diagram_type_id,
                                                 color_scheme_id)
VALUES ('62432eba-34bd-4a5e-904b-64a06068a50d',
        '57ebfe49-b3fa-43a9-a914-03510fb1e7ba');

/* UML_CLASS_MODEL */

INSERT INTO diagrams.colorscheme (id,
                                  name,
                                  fillcolor,
                                  strokecolor,
                                  textcolor)
VALUES ('1de9f16a-0e6d-4b27-a5b0-e73223f85649',
        'PRIMARY',
        '#7109AA',
        '#FFFF00',
        '#FFFF00');

INSERT INTO diagrams.colorscheme (id,
                                  name,
                                  fillcolor,
                                  strokecolor,
                                  textcolor)
VALUES ('7631c64b-d444-4f3f-ad36-6eb3807484d3',
        'SECONDARY',
        '#FFFF00',
        '#7109AA',
        '#7109AA');

INSERT INTO diagrams.diagramtype (id,
                                  type,
                                  figure,
                                  enabled)
VALUES ('fec67588-5c38-43bf-ac04-3acad0e6e615',
        'UML_CLASS_MODEL',
        '',
        FALSE);

INSERT INTO diagrams.diagram_type_color_schemes (diagram_type_id,
                                                 color_scheme_id)
VALUES ('fec67588-5c38-43bf-ac04-3acad0e6e615',
        '1de9f16a-0e6d-4b27-a5b0-e73223f85649');

INSERT INTO diagrams.diagram_type_color_schemes (diagram_type_id,
                                                 color_scheme_id)
VALUES ('fec67588-5c38-43bf-ac04-3acad0e6e615',
        '7631c64b-d444-4f3f-ad36-6eb3807484d3');

/* BPM_MODEL */

INSERT INTO diagrams.colorscheme (id,
                                  name,
                                  fillcolor,
                                  strokecolor,
                                  textcolor)
VALUES ('881b3be5-4c84-4145-96c0-6e76378fa0d0',
        'PRIMARY',
        '#00AE68',
        '#FF4C00',
        '#FF4C00');

INSERT INTO diagrams.colorscheme (id,
                                  name,
                                  fillcolor,
                                  strokecolor,
                                  textcolor)
VALUES ('7461f069-2e5b-4da7-9181-a3f3a41fb73b',
        'SECONDARY',
        '#FF4C00',
        '#00AE68',
        '#00AE68');

INSERT INTO diagrams.diagramtype (id,
                                  type,
                                  figure,
                                  enabled)
VALUES ('a3b1d451-b88d-45c9-8177-7fe4556528dc',
        'BPM_MODEL',
        '',
        FALSE);

INSERT INTO diagrams.diagram_type_color_schemes (diagram_type_id,
                                                 color_scheme_id)
VALUES ('a3b1d451-b88d-45c9-8177-7fe4556528dc',
        '881b3be5-4c84-4145-96c0-6e76378fa0d0');

INSERT INTO diagrams.diagram_type_color_schemes (diagram_type_id,
                                                 color_scheme_id)
VALUES ('a3b1d451-b88d-45c9-8177-7fe4556528dc',
        '7461f069-2e5b-4da7-9181-a3f3a41fb73b');
